package test;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Created by Administrator on 2017/2/7.
 */
public class OptionalDemo {
    public static void main(String[] args){
        Optional<Integer> possible = Optional.<Integer>of(5);
        if(possible.isPresent()){
            System.out.println(possible.get());
        }
        System.out.println(possible.asSet());

        Optional<Integer> absentValue = Optional.<Integer>absent();
        if(absentValue.isPresent()){
            System.out.println(absentValue.get());
        }
        System.out.println(absentValue.or(-1));
        System.out.println(absentValue.orNull());
        System.out.println(absentValue.asSet());

        //absent is not null
        System.out.println(Objects.firstNonNull(possible, absentValue));
        System.out.println(Objects.firstNonNull(absentValue, possible));
        System.out.println(Objects.firstNonNull(absentValue, absentValue));

        Optional<Integer> nullValue = Optional.<Integer>fromNullable(null);
        if(nullValue.isPresent()){
            System.out.println(nullValue.get());
        }
        System.out.println(nullValue.or(-1));
        System.out.println(nullValue.orNull());
        System.out.println(nullValue.asSet());

        System.out.println(Objects.firstNonNull(possible, nullValue));
        System.out.println(Objects.firstNonNull(nullValue, possible));
        System.out.println(Objects.firstNonNull(nullValue, nullValue));

        System.out.println(Objects.firstNonNull(null, 1));
        System.out.println(Objects.firstNonNull(1, null));
        System.out.println(Objects.firstNonNull(null, null));// cause a java.lang.NullPointerException
    }
}
