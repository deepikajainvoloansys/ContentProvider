package firebase.deepika.com.getdataofcontantprovider;

import android.content.ContentProviderClient;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String URL = "content://com.createprovider.studentProvider";

        Uri students = Uri.parse(URL);
        ContentProviderClient yourCR = getApplication().getContentResolver().acquireContentProviderClient(students);
        Cursor c = null;
        try {
            c = yourCR.query(students, null, null, null, "");
            if (c.moveToFirst()) {
                do{
                    Toast.makeText(this,
                            c.getString(c.getColumnIndex("_id")) +
                                    ", " +  c.getString(c.getColumnIndex( "name")) +
                                    ", " + c.getString(c.getColumnIndex( "grade")),
                            Toast.LENGTH_SHORT).show();
                } while (c.moveToNext());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }
}
