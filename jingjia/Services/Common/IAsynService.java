package Services.Common;



/**
 * Created by Administrator on 2017/1/13.
 */
public interface IAsynService {
    public abstract void run();
    public abstract IMessageState Invoke(IMessage message);
    public abstract boolean Process(IMessage message);
    public abstract boolean SetMessageQueue(MessageQueue messageQueue);
}
