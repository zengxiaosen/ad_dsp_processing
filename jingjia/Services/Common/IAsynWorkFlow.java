package Services.Common;

/**
 * Created by Administrator on 2017/1/13.
 */
public interface IAsynWorkFlow {
    public abstract IMessage  Process(IMessage message);
    public abstract boolean RegisterService(IService service);

}
