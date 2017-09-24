package lugang.app.huansi.net.lugang.event;

/**
 * Created by Tony on 2017/9/23.
 * 9:34
 */

public class SecondToFirstActivityEvent {
    public  int index;
    public Class firstClass;
    public Class secondClass;
    public Object object;

    public SecondToFirstActivityEvent(int index,Class firstClass, Class secondClass, Object object) {
        this.firstClass = firstClass;
        this.secondClass = secondClass;
        this.object = object;
        this.index=index;
    }
}
