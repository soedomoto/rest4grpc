package rest4grpc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(GrpcWebTextMediaConverterConfiguration.class)
public class GrpcWebTextConfiguration {
}
