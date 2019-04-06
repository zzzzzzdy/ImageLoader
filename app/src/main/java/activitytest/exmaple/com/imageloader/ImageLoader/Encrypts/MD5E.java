package activitytest.exmaple.com.imageloader.ImageLoader.Encrypts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import activitytest.exmaple.com.imageloader.ImageLoader.Interface.Encrypt;

/**
 * by zdy on 2019/4/3 02:39
 */
public class MD5E implements Encrypt {
    private static final MD5E instance = new MD5E();
    public static MD5E getInstance(){return instance;}
    private MD5E(){}
    @Override
    public String encode(String code) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            byte[] bytes = messageDigest.digest(code.getBytes());
            StringBuilder builder = new StringBuilder();
            for(byte bytes1:bytes){
                int number = bytes1 & 0xff;
                String s = Integer.toHexString(number);
                if(s.length()==1){
                    builder.append("0");
                }
                builder.append(s);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "233";
        }
    }
}
