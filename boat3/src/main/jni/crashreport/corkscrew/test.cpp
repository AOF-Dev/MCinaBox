#include <corkscrew/backtrace.h>
#include <corkscrew/symbol_table.h>
#include <stdio.h>
#include <stdlib.h>

int do_backtrace(float /* just to test demangling */) {
  const size_t MAX_DEPTH = 32;
  backtrace_frame_t* frames = (backtrace_frame_t*) malloc(sizeof(backtrace_frame_t) * MAX_DEPTH);
  ssize_t frame_count = unwind_backtrace(frames, 0, MAX_DEPTH);
  fprintf(stderr, "frame_count=%d\n", (int) frame_count);
  if (frame_count <= 0) {
    return frame_count;
  }

  backtrace_symbol_t* backtrace_symbols = (backtrace_symbol_t*) malloc(sizeof(backtrace_symbol_t) * frame_count);
  get_backtrace_symbols(frames, frame_count, backtrace_symbols);

  for (size_t i = 0; i < (size_t) frame_count; ++i) {
    char line[MAX_BACKTRACE_LINE_LENGTH];
    format_backtrace_line(i, &frames[i], &backtrace_symbols[i],
                          line, MAX_BACKTRACE_LINE_LENGTH);
    if (backtrace_symbols[i].symbol_name != NULL) {
      // get_backtrace_symbols found the symbol's name with dladdr(3).
      fprintf(stderr, "  %s\n", line);
    } else {
      // We don't have a symbol. Maybe this is a static symbol, and
      // we can look it up?
      symbol_table_t* symbols = NULL;
      if (backtrace_symbols[i].map_name != NULL) {
        symbols = load_symbol_table(backtrace_symbols[i].map_name);
      }
      const symbol_t* symbol = NULL;
      if (symbols != NULL) {
        symbol = find_symbol(symbols, frames[i].absolute_pc);
      }
      if (symbol != NULL) {
        int offset = frames[i].absolute_pc - symbol->start;
        fprintf(stderr, "  %s (%s%+d)\n", line, symbol->name, offset);
      } else {
        fprintf(stderr, "  %s (\?\?\?)\n", line);
      }
      free_symbol_table(symbols);
    }
  }

  free_backtrace_symbols(backtrace_symbols, frame_count);
  free(backtrace_symbols);
  free(frames);
  return frame_count;
}

struct C {
  int g(int i);
};

__attribute__ ((noinline)) int C::g(int i) {
  if (i == 0) {
    return do_backtrace(0.1);
  }
  return g(i - 1);
}

extern "C" __attribute__ ((noinline)) int f() {
  C c;
  return c.g(5);
}

int main() {
  flush_my_map_info_list();
  f();

  flush_my_map_info_list();
  f();

  return 0;
}
