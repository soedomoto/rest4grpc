package rest4grpc;

import com.google.protobuf.Message;
import io.grpc.MethodDescriptor;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@ControllerAdvice
public class GrpcWebTextRestController {
    @Autowired
    ApplicationContext applicationContext;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping(value = "/{clazz}/{method}", consumes = "*/*", produces = "*/*")
    Object post(@RequestBody byte[] data, @RequestHeader Map<String, String> headers,
                @PathVariable("clazz") String clazz, @PathVariable("method") String method) {
        DeferredResult<byte[]> deffered = new DeferredResult<byte[]>(new Long(1 * 24 * 60 * 60 * 1000));
        StringBuilder result =  new StringBuilder();

        String javaMethod = method.length() > 0 ? method.substring(0, 1).toLowerCase() + (method.length() > 1 ? method.substring(1) : "") : "";
        Map<String, Object> grpcServices = applicationContext.getBeansWithAnnotation(GRpcService.class);
        grpcServices.values().forEach(cl -> {
            try {
                Method bindService = cl.getClass().getMethod("bindService");
                ServerServiceDefinition ssd = (ServerServiceDefinition) bindService.invoke(cl);
                ServerMethodDefinition<?, ?> mt = ssd.getMethod(clazz + "/" + method);
                if (mt != null) {
                    MethodDescriptor<Message, Message> md = (MethodDescriptor<Message, Message>) mt.getMethodDescriptor();
                    Object param = md.parseRequest(new ByteArrayInputStream(data));
                    List<Method> methodsToCall = Arrays.asList(cl.getClass().getDeclaredMethods()).stream()
                            .filter(m -> m.getName().equals(javaMethod))
                            .collect(Collectors.toList());
                    if (methodsToCall.size() > 0) {
                        methodsToCall.get(0).invoke(cl, param, new StreamObserver<Message>() {
                            @Override
                            public void onNext(Message o) {
                                InputStream b = md.streamResponse(o);

                                try {
                                    ByteArrayOutputStream dataBos = new ByteArrayOutputStream();
                                    StreamUtils.copy(b, dataBos);
                                    byte[] dataBa = dataBos.toByteArray();

                                    ByteArrayOutputStream respBos = new ByteArrayOutputStream();
                                    respBos.write(0);
                                    respBos.write(0);
                                    respBos.write(0);
                                    respBos.write(0);
                                    respBos.write(dataBa.length);
                                    for (byte b1 : dataBa) {
                                        respBos.write(b1);
                                    }

                                    String x = Base64Utils.encodeToString(respBos.toByteArray());
                                    result.append(x);

                                    int a = 0;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                int a = 0;
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                deffered.setErrorResult(throwable);
                            }

                            @Override
                            public void onCompleted() {
                                deffered.setResult(result.toString().getBytes());
                            }
                        });
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("access-control-expose-headers", "custom-header-1,grpc-status,grpc-message");
        responseHeaders.set("content-type", "application/grpc-web-text");
        responseHeaders.set("grpc-accept-encoding", "gzip");
        responseHeaders.set("grpc-encoding", "identity");

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(deffered.getResult());
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public String handleHttpMediaTypeNotAcceptableException() {
        return "acceptable MIME type:" + MediaType.ALL_VALUE;
    }
}
