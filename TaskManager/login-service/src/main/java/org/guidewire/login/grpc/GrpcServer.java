//package org.guidewire.login.grpc;
//
//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//@Component
//public class GrpcServer {
//
//    private final UserServiceImpl userService;
//    @Value("${grpc.port}")
//    private Integer port;
//
//
//    @Autowired
//    public GrpcServer(UserServiceImpl userService) {
//        this.userService = userService;
//    }
//
//    /**
//     * We start the gRPC server in a separate thread to prevent it from blocking the main Spring Boot application.
//     * <p>
//     * Why?
//     * - `server.awaitTermination()` is a blocking call. If we run it on the main thread (like in @PostConstruct),
//     * it halts the Spring Boot initialization — REST controllers, beans, and endpoints never get registered.
//     * <p>
//     * How?
//     * - We use a `SingleThreadExecutor` to run the gRPC server in its own background thread.
//     * - This allows Spring Boot to continue its startup and serve REST APIs in parallel with the gRPC service.
//     * <p>
//     * When to consider an alternative?
//     * - In larger systems, it’s cleaner to run REST and gRPC in separate microservices/apps.
//     */
//
//    @PostConstruct
//    public void start() throws Exception {
//        new Thread(() -> {
//            try {
//                Server server = ServerBuilder
//                        .forPort(9093)
//                        .addService(userService)
//                        .build()
//                        .start();
//                System.out.println("gRPC server started on port 9093");
//
//                Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));
//                server.awaitTermination();
//            } catch (Exception e) {
//                System.err.println("❌ Failed to start gRPC server: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }).start();
//    }
//}
