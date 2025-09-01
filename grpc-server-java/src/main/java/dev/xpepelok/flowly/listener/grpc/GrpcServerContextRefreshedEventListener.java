package dev.xpepelok.flowly.listener.grpc;

import dev.xpepelok.flowly.configuration.credentials.GrpcProperties;
import dev.xpepelok.flowly.database.transaction.TransactionTable;
import dev.xpepelok.flowly.database.user.UserDataTable;
import dev.xpepelok.flowly.listener.ContextRefreshedEventListener;
import dev.xpepelok.flowly.service.transaction.TransactionServiceImpl;
import dev.xpepelok.flowly.service.user.UserDataServiceImpl;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrpcServerContextRefreshedEventListener implements ContextRefreshedEventListener {
    TransactionServiceImpl transactionService;
    UserDataServiceImpl userDataService;
    TransactionTable transactionTable;
    GrpcProperties grpcProperties;
    UserDataTable userDataTable;

    @Override
    public void initialize() {
        try {
            var port = grpcProperties.port();

            var server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                    .addService(userDataService)
                    .addService(transactionService)
                    .build();
            server.start();
            log.info("Server started on port {}", port);
            server.awaitTermination();

            if (server.isTerminated()) {
                transactionTable.close();
                userDataTable.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to start gRPC services", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Shutdown gRPC services...");
        }
    }
}
