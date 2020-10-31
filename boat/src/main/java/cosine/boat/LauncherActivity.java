package cosine.boat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.aof.mcinabox.definitions.models.BoatArgs;

public class LauncherActivity extends Activity {
    public BoatArgs boatArgs;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        //从序列化中取出参数对象
        boatArgs = (BoatArgs) getIntent().getSerializableExtra("LauncherConfig");

        //界面跳转至Client
        Intent intent = new Intent(this, BoatActivity.class);
        intent.putExtra("LauncherConfig", boatArgs);
        this.startActivity(intent);
        this.finish();

    }

}
