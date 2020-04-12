package rest4grpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan
public class GrpcWebTextMediaConverterConfiguration implements WebMvcConfigurer {
    @Bean
    public GrpcWebTextHttpMessageConverter converter() {
        GrpcWebTextHttpMessageConverter mc = new GrpcWebTextHttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>(mc.getSupportedMediaTypes());
        supportedMediaTypes.add(GrpcMediaType.GRPC_WEB_TEXT);
        mc.setSupportedMediaTypes(supportedMediaTypes);
        return mc;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(converter());
    }
}
