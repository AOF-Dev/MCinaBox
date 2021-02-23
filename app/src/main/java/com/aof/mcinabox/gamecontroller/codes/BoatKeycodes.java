/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.aof.mcinabox.gamecontroller.codes;

/**
 * @author elias_naur
 */

public class BoatKeycodes {
    public static final int BOAT_KEYBOARD_Kanji = 0xff21;

    public static final int BOAT_KEYBOARD_ISO_Left_Tab = 0xfe20;

    public static final int BOAT_KEYBOARD_dead_grave = 0xfe50;
    public static final int BOAT_KEYBOARD_dead_acute = 0xfe51;
    public static final int BOAT_KEYBOARD_dead_circumflex = 0xfe52;
    public static final int BOAT_KEYBOARD_dead_tilde = 0xfe53;
    public static final int BOAT_KEYBOARD_dead_macron = 0xfe54;
    public static final int BOAT_KEYBOARD_dead_breve = 0xfe55;
    public static final int BOAT_KEYBOARD_dead_abovedot = 0xfe56;
    public static final int BOAT_KEYBOARD_dead_diaeresis = 0xfe57;
    public static final int BOAT_KEYBOARD_dead_abovering = 0xfe58;
    public static final int BOAT_KEYBOARD_dead_doubleacute = 0xfe59;
    public static final int BOAT_KEYBOARD_dead_caron = 0xfe5a;
    public static final int BOAT_KEYBOARD_dead_cedilla = 0xfe5b;
    public static final int BOAT_KEYBOARD_dead_ogonek = 0xfe5c;
    public static final int BOAT_KEYBOARD_dead_iota = 0xfe5d;
    public static final int BOAT_KEYBOARD_dead_voiced_sound = 0xfe5e;
    public static final int BOAT_KEYBOARD_dead_semivoiced_sound = 0xfe5f;
    public static final int BOAT_KEYBOARD_dead_belowdot = 0xfe60;
    public static final int BOAT_KEYBOARD_dead_hook = 0xfe61;
    public static final int BOAT_KEYBOARD_dead_horn = 0xfe62;

    public static final int BOAT_KEYBOARD_BackSpace = 0xff08;
    public static final int BOAT_KEYBOARD_Tab = 0xff09;
    public static final int BOAT_KEYBOARD_Linefeed = 0xff0a;
    public static final int BOAT_KEYBOARD_Clear = 0xff0b;
    public static final int BOAT_KEYBOARD_Return = 0xff0d;
    public static final int BOAT_KEYBOARD_Pause = 0xff13;
    public static final int BOAT_KEYBOARD_Scroll_Lock = 0xff14;
    public static final int BOAT_KEYBOARD_Sys_Req = 0xff15;
    public static final int BOAT_KEYBOARD_Escape = 0xff1b;
    public static final int BOAT_KEYBOARD_Delete = 0xffff;

    public static final int BOAT_KEYBOARD_Home = 0xff50;
    public static final int BOAT_KEYBOARD_Left = 0xff51;
    public static final int BOAT_KEYBOARD_Up = 0xff52;
    public static final int BOAT_KEYBOARD_Right = 0xff53;
    public static final int BOAT_KEYBOARD_Down = 0xff54;
    public static final int BOAT_KEYBOARD_Prior = 0xff55;
    public static final int BOAT_KEYBOARD_Page_Up = 0xff55;
    public static final int BOAT_KEYBOARD_Next = 0xff56;
    public static final int BOAT_KEYBOARD_Page_Down = 0xff56;
    public static final int BOAT_KEYBOARD_End = 0xff57;
    public static final int BOAT_KEYBOARD_Begin = 0xff58;


    /* Misc functions */

    public static final int BOAT_KEYBOARD_Select = 0xff60;
    public static final int BOAT_KEYBOARD_Print = 0xff61;
    public static final int BOAT_KEYBOARD_Execute = 0xff62;
    public static final int BOAT_KEYBOARD_Insert = 0xff63;
    public static final int BOAT_KEYBOARD_Undo = 0xff65;
    public static final int BOAT_KEYBOARD_Redo = 0xff66;
    public static final int BOAT_KEYBOARD_Menu = 0xff67;
    public static final int BOAT_KEYBOARD_Find = 0xff68;
    public static final int BOAT_KEYBOARD_Cancel = 0xff69;
    public static final int BOAT_KEYBOARD_Help = 0xff6a;
    public static final int BOAT_KEYBOARD_Break = 0xff6b;
    public static final int BOAT_KEYBOARD_Mode_switch = 0xff7e;
    public static final int BOAT_KEYBOARD_script_switch = 0xff7e;
    public static final int BOAT_KEYBOARD_Num_Lock = 0xff7f;

    /* Keypad functions, keypad numbers cleverly chosen to map to ASCII */

    public static final int BOAT_KEYBOARD_KP_Space = 0xff80;
    public static final int BOAT_KEYBOARD_KP_Tab = 0xff89;
    public static final int BOAT_KEYBOARD_KP_Enter = 0xff8d;
    public static final int BOAT_KEYBOARD_KP_F1 = 0xff91;
    public static final int BOAT_KEYBOARD_KP_F2 = 0xff92;
    public static final int BOAT_KEYBOARD_KP_F3 = 0xff93;
    public static final int BOAT_KEYBOARD_KP_F4 = 0xff94;
    public static final int BOAT_KEYBOARD_KP_Home = 0xff95;
    public static final int BOAT_KEYBOARD_KP_Left = 0xff96;
    public static final int BOAT_KEYBOARD_KP_Up = 0xff97;
    public static final int BOAT_KEYBOARD_KP_Right = 0xff98;
    public static final int BOAT_KEYBOARD_KP_Down = 0xff99;
    public static final int BOAT_KEYBOARD_KP_Prior = 0xff9a;
    public static final int BOAT_KEYBOARD_KP_Page_Up = 0xff9a;
    public static final int BOAT_KEYBOARD_KP_Next = 0xff9b;
    public static final int BOAT_KEYBOARD_KP_Page_Down = 0xff9b;
    public static final int BOAT_KEYBOARD_KP_End = 0xff9c;
    public static final int BOAT_KEYBOARD_KP_Begin = 0xff9d;
    public static final int BOAT_KEYBOARD_KP_Insert = 0xff9e;
    public static final int BOAT_KEYBOARD_KP_Delete = 0xff9f;
    public static final int BOAT_KEYBOARD_KP_Equal = 0xffbd;
    public static final int BOAT_KEYBOARD_KP_Multiply = 0xffaa;
    public static final int BOAT_KEYBOARD_KP_Add = 0xffab;
    public static final int BOAT_KEYBOARD_KP_Separator = 0xffac;
    public static final int BOAT_KEYBOARD_KP_Subtract = 0xffad;
    public static final int BOAT_KEYBOARD_KP_Decimal = 0xffae;
    public static final int BOAT_KEYBOARD_KP_Divide = 0xffaf;

    public static final int BOAT_KEYBOARD_KP_0 = 0xffb0;
    public static final int BOAT_KEYBOARD_KP_1 = 0xffb1;
    public static final int BOAT_KEYBOARD_KP_2 = 0xffb2;
    public static final int BOAT_KEYBOARD_KP_3 = 0xffb3;
    public static final int BOAT_KEYBOARD_KP_4 = 0xffb4;
    public static final int BOAT_KEYBOARD_KP_5 = 0xffb5;
    public static final int BOAT_KEYBOARD_KP_6 = 0xffb6;
    public static final int BOAT_KEYBOARD_KP_7 = 0xffb7;
    public static final int BOAT_KEYBOARD_KP_8 = 0xffb8;
    public static final int BOAT_KEYBOARD_KP_9 = 0xffb9;



    /*
     * Auxilliary functions; note the duplicate definitions for left and right
     * function keys;  Sun keyboards and a few other manufactures have such
     * function key groups on the left and/or right sides of the keyboard.
     * We've not found a keyboard with more than 35 function keys total.
     */

    public static final int BOAT_KEYBOARD_F1 = 0xffbe;
    public static final int BOAT_KEYBOARD_F2 = 0xffbf;
    public static final int BOAT_KEYBOARD_F3 = 0xffc0;
    public static final int BOAT_KEYBOARD_F4 = 0xffc1;
    public static final int BOAT_KEYBOARD_F5 = 0xffc2;
    public static final int BOAT_KEYBOARD_F6 = 0xffc3;
    public static final int BOAT_KEYBOARD_F7 = 0xffc4;
    public static final int BOAT_KEYBOARD_F8 = 0xffc5;
    public static final int BOAT_KEYBOARD_F9 = 0xffc6;
    public static final int BOAT_KEYBOARD_F10 = 0xffc7;
    public static final int BOAT_KEYBOARD_F11 = 0xffc8;
    public static final int BOAT_KEYBOARD_L1 = 0xffc8;
    public static final int BOAT_KEYBOARD_F12 = 0xffc9;
    public static final int BOAT_KEYBOARD_L2 = 0xffc9;
    public static final int BOAT_KEYBOARD_F13 = 0xffca;
    public static final int BOAT_KEYBOARD_L3 = 0xffca;
    public static final int BOAT_KEYBOARD_F14 = 0xffcb;
    public static final int BOAT_KEYBOARD_L4 = 0xffcb;
    public static final int BOAT_KEYBOARD_F15 = 0xffcc;
    public static final int BOAT_KEYBOARD_L5 = 0xffcc;
    public static final int BOAT_KEYBOARD_F16 = 0xffcd;
    public static final int BOAT_KEYBOARD_L6 = 0xffcd;
    public static final int BOAT_KEYBOARD_F17 = 0xffce;
    public static final int BOAT_KEYBOARD_L7 = 0xffce;
    public static final int BOAT_KEYBOARD_F18 = 0xffcf;
    public static final int BOAT_KEYBOARD_L8 = 0xffcf;
    public static final int BOAT_KEYBOARD_F19 = 0xffd0;
    public static final int BOAT_KEYBOARD_L9 = 0xffd0;
    public static final int BOAT_KEYBOARD_F20 = 0xffd1;
    public static final int BOAT_KEYBOARD_L10 = 0xffd1;
    public static final int BOAT_KEYBOARD_F21 = 0xffd2;
    public static final int BOAT_KEYBOARD_R1 = 0xffd2;
    public static final int BOAT_KEYBOARD_F22 = 0xffd3;
    public static final int BOAT_KEYBOARD_R2 = 0xffd3;
    public static final int BOAT_KEYBOARD_F23 = 0xffd4;
    public static final int BOAT_KEYBOARD_R3 = 0xffd4;
    public static final int BOAT_KEYBOARD_F24 = 0xffd5;
    public static final int BOAT_KEYBOARD_R4 = 0xffd5;
    public static final int BOAT_KEYBOARD_F25 = 0xffd6;
    public static final int BOAT_KEYBOARD_R5 = 0xffd6;
    public static final int BOAT_KEYBOARD_F26 = 0xffd7;
    public static final int BOAT_KEYBOARD_R6 = 0xffd7;
    public static final int BOAT_KEYBOARD_F27 = 0xffd8;
    public static final int BOAT_KEYBOARD_R7 = 0xffd8;
    public static final int BOAT_KEYBOARD_F28 = 0xffd9;
    public static final int BOAT_KEYBOARD_R8 = 0xffd9;
    public static final int BOAT_KEYBOARD_F29 = 0xffda;
    public static final int BOAT_KEYBOARD_R9 = 0xffda;
    public static final int BOAT_KEYBOARD_F30 = 0xffdb;
    public static final int BOAT_KEYBOARD_R10 = 0xffdb;
    public static final int BOAT_KEYBOARD_F31 = 0xffdc;
    public static final int BOAT_KEYBOARD_R11 = 0xffdc;
    public static final int BOAT_KEYBOARD_F32 = 0xffdd;
    public static final int BOAT_KEYBOARD_R12 = 0xffdd;
    public static final int BOAT_KEYBOARD_F33 = 0xffde;
    public static final int BOAT_KEYBOARD_R13 = 0xffde;
    public static final int BOAT_KEYBOARD_F34 = 0xffdf;
    public static final int BOAT_KEYBOARD_R14 = 0xffdf;
    public static final int BOAT_KEYBOARD_F35 = 0xffe0;
    public static final int BOAT_KEYBOARD_R15 = 0xffe0;

    /* Modifiers */

    public static final int BOAT_KEYBOARD_Shift_L = 0xffe1;
    public static final int BOAT_KEYBOARD_Shift_R = 0xffe2;
    public static final int BOAT_KEYBOARD_Control_L = 0xffe3;
    public static final int BOAT_KEYBOARD_Control_R = 0xffe4;
    public static final int BOAT_KEYBOARD_Caps_Lock = 0xffe5;
    public static final int BOAT_KEYBOARD_Shift_Lock = 0xffe6;

    public static final int BOAT_KEYBOARD_Meta_L = 0xffe7;
    public static final int BOAT_KEYBOARD_Meta_R = 0xffe8;
    public static final int BOAT_KEYBOARD_Alt_L = 0xffe9;
    public static final int BOAT_KEYBOARD_Alt_R = 0xffea;
    public static final int BOAT_KEYBOARD_Super_L = 0xffeb;
    public static final int BOAT_KEYBOARD_Super_R = 0xffec;
    public static final int BOAT_KEYBOARD_Hyper_L = 0xffed;
    public static final int BOAT_KEYBOARD_Hyper_R = 0xffee;
    public static final int BOAT_KEYBOARD_space = 0x0020;
    public static final int BOAT_KEYBOARD_exclam = 0x0021;
    public static final int BOAT_KEYBOARD_quotedbl = 0x0022;
    public static final int BOAT_KEYBOARD_numbersign = 0x0023;
    public static final int BOAT_KEYBOARD_dollar = 0x0024;
    public static final int BOAT_KEYBOARD_percent = 0x0025;
    public static final int BOAT_KEYBOARD_ampersand = 0x0026;
    public static final int BOAT_KEYBOARD_apostrophe = 0x0027;
    public static final int BOAT_KEYBOARD_quoteright = 0x0027;
    public static final int BOAT_KEYBOARD_parenleft = 0x0028;
    public static final int BOAT_KEYBOARD_parenright = 0x0029;
    public static final int BOAT_KEYBOARD_asterisk = 0x002a;
    public static final int BOAT_KEYBOARD_plus = 0x002b;
    public static final int BOAT_KEYBOARD_comma = 0x002c;
    public static final int BOAT_KEYBOARD_minus = 0x002d;
    public static final int BOAT_KEYBOARD_period = 0x002e;
    public static final int BOAT_KEYBOARD_slash = 0x002f;

    public static final int BOAT_KEYBOARD_0 = 0x0030;
    public static final int BOAT_KEYBOARD_1 = 0x0031;
    public static final int BOAT_KEYBOARD_2 = 0x0032;
    public static final int BOAT_KEYBOARD_3 = 0x0033;
    public static final int BOAT_KEYBOARD_4 = 0x0034;
    public static final int BOAT_KEYBOARD_5 = 0x0035;
    public static final int BOAT_KEYBOARD_6 = 0x0036;
    public static final int BOAT_KEYBOARD_7 = 0x0037;
    public static final int BOAT_KEYBOARD_8 = 0x0038;
    public static final int BOAT_KEYBOARD_9 = 0x0039;
    public static final int BOAT_KEYBOARD_colon = 0x003a;
    public static final int BOAT_KEYBOARD_semicolon = 0x003b;
    public static final int BOAT_KEYBOARD_less = 0x003c;
    public static final int BOAT_KEYBOARD_equal = 0x003d;
    public static final int BOAT_KEYBOARD_greater = 0x003e;
    public static final int BOAT_KEYBOARD_question = 0x003f;
    public static final int BOAT_KEYBOARD_at = 0x0040;
    public static final int BOAT_KEYBOARD_A = 0x0041;
    public static final int BOAT_KEYBOARD_B = 0x0042;
    public static final int BOAT_KEYBOARD_C = 0x0043;
    public static final int BOAT_KEYBOARD_D = 0x0044;
    public static final int BOAT_KEYBOARD_E = 0x0045;
    public static final int BOAT_KEYBOARD_F = 0x0046;
    public static final int BOAT_KEYBOARD_G = 0x0047;
    public static final int BOAT_KEYBOARD_H = 0x0048;
    public static final int BOAT_KEYBOARD_I = 0x0049;
    public static final int BOAT_KEYBOARD_J = 0x004a;
    public static final int BOAT_KEYBOARD_K = 0x004b;
    public static final int BOAT_KEYBOARD_L = 0x004c;
    public static final int BOAT_KEYBOARD_M = 0x004d;
    public static final int BOAT_KEYBOARD_N = 0x004e;
    public static final int BOAT_KEYBOARD_O = 0x004f;
    public static final int BOAT_KEYBOARD_P = 0x0050;
    public static final int BOAT_KEYBOARD_Q = 0x0051;
    public static final int BOAT_KEYBOARD_R = 0x0052;
    public static final int BOAT_KEYBOARD_S = 0x0053;
    public static final int BOAT_KEYBOARD_T = 0x0054;
    public static final int BOAT_KEYBOARD_U = 0x0055;
    public static final int BOAT_KEYBOARD_V = 0x0056;
    public static final int BOAT_KEYBOARD_W = 0x0057;
    public static final int BOAT_KEYBOARD_X = 0x0058;
    public static final int BOAT_KEYBOARD_Y = 0x0059;
    public static final int BOAT_KEYBOARD_Z = 0x005a;
    public static final int BOAT_KEYBOARD_bracketleft = 0x005b;
    public static final int BOAT_KEYBOARD_backslash = 0x005c;
    public static final int BOAT_KEYBOARD_bracketright = 0x005d;
    public static final int BOAT_KEYBOARD_asciicircum = 0x005e;
    public static final int BOAT_KEYBOARD_underscore = 0x005f;
    public static final int BOAT_KEYBOARD_grave = 0x0060;
    public static final int BOAT_KEYBOARD_quoteleft = 0x0060;
    public static final int BOAT_KEYBOARD_a = 0x0061;
    public static final int BOAT_KEYBOARD_b = 0x0062;
    public static final int BOAT_KEYBOARD_c = 0x0063;
    public static final int BOAT_KEYBOARD_d = 0x0064;
    public static final int BOAT_KEYBOARD_e = 0x0065;
    public static final int BOAT_KEYBOARD_f = 0x0066;
    public static final int BOAT_KEYBOARD_g = 0x0067;
    public static final int BOAT_KEYBOARD_h = 0x0068;
    public static final int BOAT_KEYBOARD_i = 0x0069;
    public static final int BOAT_KEYBOARD_j = 0x006a;
    public static final int BOAT_KEYBOARD_k = 0x006b;
    public static final int BOAT_KEYBOARD_l = 0x006c;
    public static final int BOAT_KEYBOARD_m = 0x006d;
    public static final int BOAT_KEYBOARD_n = 0x006e;
    public static final int BOAT_KEYBOARD_o = 0x006f;
    public static final int BOAT_KEYBOARD_p = 0x0070;
    public static final int BOAT_KEYBOARD_q = 0x0071;
    public static final int BOAT_KEYBOARD_r = 0x0072;
    public static final int BOAT_KEYBOARD_s = 0x0073;
    public static final int BOAT_KEYBOARD_t = 0x0074;
    public static final int BOAT_KEYBOARD_u = 0x0075;
    public static final int BOAT_KEYBOARD_v = 0x0076;
    public static final int BOAT_KEYBOARD_w = 0x0077;
    public static final int BOAT_KEYBOARD_x = 0x0078;
    public static final int BOAT_KEYBOARD_y = 0x0079;
    public static final int BOAT_KEYBOARD_z = 0x007a;
    public static final int BOAT_KEYBOARD_braceleft = 0x007b;
    public static final int BOAT_KEYBOARD_bar = 0x007c;
    public static final int BOAT_KEYBOARD_braceright = 0x007d;
    public static final int BOAT_KEYBOARD_asciitilde = 0x007e;

    public static final int BOAT_KEYBOARD_nobreakspace = 0x00a0;
    public static final int BOAT_KEYBOARD_exclamdown = 0x00a1;
    public static final int BOAT_KEYBOARD_cent = 0x00a2;
    public static final int BOAT_KEYBOARD_sterling = 0x00a3;
    public static final int BOAT_KEYBOARD_currency = 0x00a4;
    public static final int BOAT_KEYBOARD_yen = 0x00a5;
    public static final int BOAT_KEYBOARD_brokenbar = 0x00a6;
    public static final int BOAT_KEYBOARD_section = 0x00a7;
    public static final int BOAT_KEYBOARD_diaeresis = 0x00a8;
    public static final int BOAT_KEYBOARD_copyright = 0x00a9;
    public static final int BOAT_KEYBOARD_ordfeminine = 0x00aa;
    public static final int BOAT_KEYBOARD_guillemotleft = 0x00ab;
    public static final int BOAT_KEYBOARD_notsign = 0x00ac;
    public static final int BOAT_KEYBOARD_hyphen = 0x00ad;
    public static final int BOAT_KEYBOARD_registered = 0x00ae;
    public static final int BOAT_KEYBOARD_macron = 0x00af;
    public static final int BOAT_KEYBOARD_degree = 0x00b0;
    public static final int BOAT_KEYBOARD_plusminus = 0x00b1;
    public static final int BOAT_KEYBOARD_twosuperior = 0x00b2;
    public static final int BOAT_KEYBOARD_threesuperior = 0x00b3;
    public static final int BOAT_KEYBOARD_acute = 0x00b4;
    public static final int BOAT_KEYBOARD_mu = 0x00b5;
    public static final int BOAT_KEYBOARD_paragraph = 0x00b6;
    public static final int BOAT_KEYBOARD_periodcentered = 0x00b7;
    public static final int BOAT_KEYBOARD_cedilla = 0x00b8;
    public static final int BOAT_KEYBOARD_onesuperior = 0x00b9;
    public static final int BOAT_KEYBOARD_masculine = 0x00ba;
    public static final int BOAT_KEYBOARD_guillemotright = 0x00bb;
    public static final int BOAT_KEYBOARD_onequarter = 0x00bc;
    public static final int BOAT_KEYBOARD_onehalf = 0x00bd;
    public static final int BOAT_KEYBOARD_threequarters = 0x00be;
    public static final int BOAT_KEYBOARD_questiondown = 0x00bf;
    public static final int BOAT_KEYBOARD_Agrave = 0x00c0;
    public static final int BOAT_KEYBOARD_Aacute = 0x00c1;
    public static final int BOAT_KEYBOARD_Acircumflex = 0x00c2;
    public static final int BOAT_KEYBOARD_Atilde = 0x00c3;
    public static final int BOAT_KEYBOARD_Adiaeresis = 0x00c4;
    public static final int BOAT_KEYBOARD_Aring = 0x00c5;
    public static final int BOAT_KEYBOARD_AE = 0x00c6;
    public static final int BOAT_KEYBOARD_Ccedilla = 0x00c7;
    public static final int BOAT_KEYBOARD_Egrave = 0x00c8;
    public static final int BOAT_KEYBOARD_Eacute = 0x00c9;
    public static final int BOAT_KEYBOARD_Ecircumflex = 0x00ca;
    public static final int BOAT_KEYBOARD_Ediaeresis = 0x00cb;
    public static final int BOAT_KEYBOARD_Igrave = 0x00cc;
    public static final int BOAT_KEYBOARD_Iacute = 0x00cd;
    public static final int BOAT_KEYBOARD_Icircumflex = 0x00ce;
    public static final int BOAT_KEYBOARD_Idiaeresis = 0x00cf;
    public static final int BOAT_KEYBOARD_ETH = 0x00d0;
    public static final int BOAT_KEYBOARD_Eth = 0x00d0;
    public static final int BOAT_KEYBOARD_Ntilde = 0x00d1;
    public static final int BOAT_KEYBOARD_Ograve = 0x00d2;
    public static final int BOAT_KEYBOARD_Oacute = 0x00d3;
    public static final int BOAT_KEYBOARD_Ocircumflex = 0x00d4;
    public static final int BOAT_KEYBOARD_Otilde = 0x00d5;
    public static final int BOAT_KEYBOARD_Odiaeresis = 0x00d6;
    public static final int BOAT_KEYBOARD_multiply = 0x00d7;
    public static final int BOAT_KEYBOARD_Oslash = 0x00d8;
    public static final int BOAT_KEYBOARD_Ooblique = 0x00d8;
    public static final int BOAT_KEYBOARD_Ugrave = 0x00d9;
    public static final int BOAT_KEYBOARD_Uacute = 0x00da;
    public static final int BOAT_KEYBOARD_Ucircumflex = 0x00db;
    public static final int BOAT_KEYBOARD_Udiaeresis = 0x00dc;
    public static final int BOAT_KEYBOARD_Yacute = 0x00dd;
    public static final int BOAT_KEYBOARD_THORN = 0x00de;
    public static final int BOAT_KEYBOARD_Thorn = 0x00de;
    public static final int BOAT_KEYBOARD_ssharp = 0x00df;
    public static final int BOAT_KEYBOARD_agrave = 0x00e0;
    public static final int BOAT_KEYBOARD_aacute = 0x00e1;
    public static final int BOAT_KEYBOARD_acircumflex = 0x00e2;
    public static final int BOAT_KEYBOARD_atilde = 0x00e3;
    public static final int BOAT_KEYBOARD_adiaeresis = 0x00e4;
    public static final int BOAT_KEYBOARD_aring = 0x00e5;
    public static final int BOAT_KEYBOARD_ae = 0x00e6;
    public static final int BOAT_KEYBOARD_ccedilla = 0x00e7;
    public static final int BOAT_KEYBOARD_egrave = 0x00e8;
    public static final int BOAT_KEYBOARD_eacute = 0x00e9;
    public static final int BOAT_KEYBOARD_ecircumflex = 0x00ea;
    public static final int BOAT_KEYBOARD_ediaeresis = 0x00eb;
    public static final int BOAT_KEYBOARD_igrave = 0x00ec;
    public static final int BOAT_KEYBOARD_iacute = 0x00ed;
    public static final int BOAT_KEYBOARD_icircumflex = 0x00ee;
    public static final int BOAT_KEYBOARD_idiaeresis = 0x00ef;
    public static final int BOAT_KEYBOARD_eth = 0x00f0;
    public static final int BOAT_KEYBOARD_ntilde = 0x00f1;
    public static final int BOAT_KEYBOARD_ograve = 0x00f2;
    public static final int BOAT_KEYBOARD_oacute = 0x00f3;
    public static final int BOAT_KEYBOARD_ocircumflex = 0x00f4;
    public static final int BOAT_KEYBOARD_otilde = 0x00f5;
    public static final int BOAT_KEYBOARD_odiaeresis = 0x00f6;
    public static final int BOAT_KEYBOARD_division = 0x00f7;
    public static final int BOAT_KEYBOARD_oslash = 0x00f8;
    public static final int BOAT_KEYBOARD_ooblique = 0x00f8;
    public static final int BOAT_KEYBOARD_ugrave = 0x00f9;
    public static final int BOAT_KEYBOARD_uacute = 0x00fa;
    public static final int BOAT_KEYBOARD_ucircumflex = 0x00fb;
    public static final int BOAT_KEYBOARD_udiaeresis = 0x00fc;
    public static final int BOAT_KEYBOARD_yacute = 0x00fd;
    public static final int BOAT_KEYBOARD_thorn = 0x00fe;
    public static final int BOAT_KEYBOARD_ydiaeresis = 0x00ff;

    public static final int BOAT_KEYBOARD_ISO_Level3_Shift = 0xfe03;
}
