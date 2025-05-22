//package org.guidewire.login.grpc;
//
//import io.grpc.stub.StreamObserver;
//import org.guidewire.login.model.User;
//import org.guidewire.login.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//
//@Service
//public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public void getUsers(UserIdsRequest request, StreamObserver<UserResponseList> responseObserver) {
//        List<UUID> userIds = request.getIdsList().stream().map(UUID::fromString).collect(Collectors.toList());
//
//        List<UserRpcResponse> users = userRepository.findAllById(userIds)
//                .stream()
//                .map(this::mapToGrpcUser)
//                .collect(Collectors.toList());
//
//        UserResponseList response = UserResponseList.newBuilder()
//                .addAllUsers(users)
//                .build();
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//    }
//
//    private UserRpcResponse mapToGrpcUser(User user) {
//        return UserRpcResponse.newBuilder()
//                .setId(user.getId().toString())
//                .setName(user.getUsername())
//                .setPhone(user.getPhoneNumber())
//                .setMailId(user.getEmail())
//                .build();
//    }
//}
