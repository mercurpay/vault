package vault.domain;

import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vault.DataByToken;
import vault.RequestNewToken;
import vault.Token;
import vault.VaultServiceGrpc;

/** @author claudioed on 2019-05-19. Project vault */
@GrpcService
public class VaultService extends VaultServiceGrpc.VaultServiceImplBase {

  private static final Logger log = LoggerFactory.getLogger(VaultService.class);

  @Inject private CryptoService cryptoService;

  public void newToken(RequestNewToken request, StreamObserver<Token> responseObserver) {
    try {
      log.info("Receiving request to create new token...");
      final String data = request.getCustomerId() + ":" + request.getCard() + ":" + request.getIssuer();
      final String encryptedData = this.cryptoService.encrypt(data);
      final Token token = Token.newBuilder().setValue(encryptedData).build();
      log.info("Token created successfully TOKEN {}",token.getValue());
      responseObserver.onNext(token);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Error to create token for customer id {}",request.getCustomerId());
      throw new RuntimeException("Error to create token", e);
    }
  }

  @Override
  public void fromToken(Token request, StreamObserver<DataByToken> responseObserver) {
    try {
      log.info("Receiving request to open token...");
      final String rawData = this.cryptoService.decrypt(request.getValue());
      final String[] data = rawData.split(":");
      final DataByToken dataByToken =
          DataByToken.newBuilder()
              .setCustomerId(data[0])
              .setCard(data[1])
              .setIssuer(data[2])
              .build();
      log.info("Token opened successfully customer id {} issuer {}",dataByToken.getCustomerId(),dataByToken.getIssuer());
      responseObserver.onNext(dataByToken);
      responseObserver.onCompleted();
    } catch (Exception ex) {
      log.error("Error to open token {}",request.getValue());
      throw new RuntimeException("Error to decrypt token", ex);
    }
  }
}
