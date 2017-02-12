package processing_pcData;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Administrator on 2017/2/11.
 */
public class ProductModel implements WritableComparable<ProductModel> {
    private String id;
    private String name;
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductModel)) return false;

        ProductModel that = (ProductModel) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        return price.equals(that.price);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    public int compareTo(ProductModel o) {
        if(this == o){
            return 0;
        }
        int tmp = this.id.compareTo(o.id);
        if(tmp != 0){
            return tmp;
        }
        tmp = this.name.compareTo(o.name);
        if(tmp != 0){
            return  tmp;
        }

        tmp = this.price.compareTo(o.price);
        return tmp;
    }

    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.id);
        out.writeUTF(this.name);
        out.writeUTF(this.price);
    }

    public void readFields(DataInput in) throws IOException {
        this.id = in.readUTF();
        this.name = in.readUTF();
        this.price = in.readUTF();
    }
}
