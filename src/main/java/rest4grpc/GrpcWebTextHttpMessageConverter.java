package rest4grpc;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GrpcWebTextHttpMessageConverter extends ByteArrayHttpMessageConverter {

    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage inputMessage) throws IOException {
        byte[] encoded = super.readInternal(clazz, inputMessage);
        byte[] decoded = Base64Utils.decode(encoded);

        // get length of data
        int length = 0;
        for (int i = 0; i < decoded.length; i++) {
            if (decoded[i] == (byte) 0) continue;
            if (decoded[i] != (byte) 0) {
                length = decoded[i];

                ByteArrayOutputStream bObj = new ByteArrayOutputStream();
                for (int j = i + 1; j < i + 1 + length; j++) {
                    bObj.write(decoded[j]);
                }
                byte[] data = bObj.toByteArray();
                return data;
            }
        }

        return null;
    }
}
