package com.example.des;
/*
* 2020-06-06
* 邮箱1132269071@qq.com
 */

import android.os.Bundle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    //实例化控件
    private EditText entx;
    private TextView detx;
    private EditText keytx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //赋值文本框资源
        entx = (EditText)findViewById(R.id.encryptText);
        detx = (TextView)findViewById(R.id.decryptText);
        keytx = (EditText)findViewById(R.id.keyText);
        //detx.setText("");
        //注册监听器
        Button enbtn = (Button)findViewById(R.id.encryptBtn);
        enbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detx.setText("");
                try {
                    Encrypt(null);//加密功能
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Button debtn = (Button)findViewById(R.id.decryptBtn);
        debtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detx.setText("");
                try {
                    Decrypt(null);//解密功能
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void Encrypt(View view) throws Exception {
        try {
            String strE = entx.getText().toString();
            if(strE.length() == 0){
                throw new Exception();
            }
            String key = keytx.getText().toString();
            String str1E = DES.encryptDES(strE, key);
            detx.setText(str1E);
        } catch (Exception e) {
            e.printStackTrace();
            Toast t1 = Toast.makeText(MainActivity.this, "加密失败", Toast.LENGTH_SHORT);
            t1.setGravity(Gravity.BOTTOM,0,500);
            t1.show();

        }
    }

    public void Decrypt(View view) throws Exception {
        try{
            String strD = entx.getText().toString();
            String key = keytx.getText().toString();
            String str1D = DES.decryptDES(strD,key);
        detx.setText(str1D);
        } catch (Exception e){
            e.printStackTrace();
            Toast t2 = Toast.makeText(MainActivity.this, "解密失败", Toast.LENGTH_SHORT);
            t2.setGravity(Gravity.BOTTOM,0,500);
            t2.show();
        }
    }
}

//---------------------------------------------------------------------------------------
//以下类参考：https://blog.csdn.net/yakedar/article/details/9721225
class DES {
    private static byte[] iv = {1,2,3,4,5,6,7,8};
    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
//		IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

        return Base64.encode(encryptedData);
    }
    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        byte[] byteMi = new Base64().decode(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
//		IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData);
    }
}

class Base64 {
    private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    /**
     * data[]进行编码
     * @param data
     * @return
     */
    public static String encode(byte[] data) {
        int start = 0;
        int len = data.length;
        StringBuffer buf = new StringBuffer(data.length * 3 / 2);

        int end = len - 3;
        int i = start;
        int n = 0;

        while (i <= end) {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 0x0ff) << 8)
                    | (((int) data[i + 2]) & 0x0ff);

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append(legalChars[d & 63]);

            i += 3;

            if (n++ >= 14) {
                n = 0;
                buf.append(" ");
            }
        }

        if (i == start + len - 2) {
            int d = ((((int) data[i]) & 0x0ff) << 16)
                    | ((((int) data[i + 1]) & 255) << 8);

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append(legalChars[(d >> 6) & 63]);
            buf.append("=");
        } else if (i == start + len - 1) {
            int d = (((int) data[i]) & 0x0ff) << 16;

            buf.append(legalChars[(d >> 18) & 63]);
            buf.append(legalChars[(d >> 12) & 63]);
            buf.append("==");
        }

        return buf.toString();
    }

    private static int decode(char c) {
        if (c >= 'A' && c <= 'Z')
            return ((int) c) - 65;
        else if (c >= 'a' && c <= 'z')
            return ((int) c) - 97 + 26;
        else if (c >= '0' && c <= '9')
            return ((int) c) - 48 + 26 + 26;
        else
            switch (c) {
                case '+':
                    return 62;
                case '/':
                    return 63;
                case '=':
                    return 0;
                default:
                    throw new RuntimeException("unexpected code: " + c);
            }
    }

    /**
     * Decodes the given Base64 encoded String to a new byte array. The byte
     * array holding the decoded data is returned.
     */

    public static byte[] decode(String s) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            decode(s, bos);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        byte[] decodedBytes = bos.toByteArray();
        try {
            bos.close();
            bos = null;
        } catch (IOException ex) {
            System.err.println("Error while decoding BASE64: " + ex.toString());
        }
        return decodedBytes;
    }

    private static void decode(String s, OutputStream os) throws IOException {
        int i = 0;

        int len = s.length();

        while (true) {
            while (i < len && s.charAt(i) <= ' ')
                i++;

            if (i == len)
                break;

            int tri = (decode(s.charAt(i)) << 18)
                    + (decode(s.charAt(i + 1)) << 12)
                    + (decode(s.charAt(i + 2)) << 6)
                    + (decode(s.charAt(i + 3)));

            os.write((tri >> 16) & 255);
            if (s.charAt(i + 2) == '=')
                break;
            os.write((tri >> 8) & 255);
            if (s.charAt(i + 3) == '=')
                break;
            os.write(tri & 255);

            i += 4;
        }
    }

}