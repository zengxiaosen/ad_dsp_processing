package Services.Common;



/**
 * Created by Administrator on 2017/1/13.
 */
public interface IMessageHandler extends Runnable{
    public abstract boolean SetMessageQueue(MessageQueue messageQueue);
}
