package habib.voip.base;

import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by Habib on 1.12.2014.
 */
public abstract class AsyncBase<T,Q,K> extends AsyncTask<T,Q,K> {

    public void executeContent(T... content){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, content);
        }else{
            this.execute(content);
        }
    }
}
