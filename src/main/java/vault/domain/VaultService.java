package vault.domain;

import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;
import javax.inject.Inject;
import vault.DataByToken;
import vault.RequestNewToken;
import vault.Token;
import vault.VaultServiceGrpc;

/** @author claudioed on 2019-05-19. Project vault */
@GrpcService
public class VaultService extends VaultServiceGrpc.VaultServiceImplBase {

  @Inject private CryptoService cryptoService;

  public void newToken(RequestNewToken request, StreamObserver<Token> responseObserver) {
    try {
      final String data = request.getCustomerId() + ":" + request.getCard() + ":" + request.getIssuer();
      final String encryptedData = this.cryptoService.encrypt(data);
      final Token token = Token.newBuilder().setValue(encryptedData).build();
      responseObserver.onNext(token);
      responseObserver.onCompleted();
    } catch (Exception e) {
      throw new RuntimeException("Error to create token", e);
    }
  }

  @Override
  public void fromToken(Token request, StreamObserver<DataByToken> responseObserver) {
    try{
    final String rawData = this.cryptoService.decrypt(request.getValue());
    final String[] data = rawData.split(":");
      final DataByToken dataByToken = DataByToken.newBuilder().setCustomerId(data[0]).setCard(data[1])
          .setIssuer(data[2]).build();
      responseObserver.onNext(dataByToken);
      responseObserver.onCompleted();
    }catch (Exception ex){
      throw new RuntimeException("Error to decrypt token", ex);
    }
  }

}
