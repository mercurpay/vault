package vault.domain;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;
import java.security.GeneralSecurityException;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author claudioed on 2019-05-19. Project vault */
@Singleton
public class CryptoService {

  private static final Logger log = LoggerFactory.getLogger(CryptoService.class);

  private KeyTemplate keyTemplate = AeadKeyTemplates.AES256_EAX;

  private final KeysetHandle keysetHandle;

  private final Aead aead;

  private final String SECRET = "mercur@pay";

  public CryptoService() throws GeneralSecurityException {
    AeadConfig.register();
    this.keysetHandle = KeysetHandle.generateNew(keyTemplate);
    this.aead = AeadFactory.getPrimitive(keysetHandle);
  }

  public String encrypt(String rawData) throws GeneralSecurityException {
    log.info("Encrypting data {}",rawData);
    final String cipheredData = this.aead.encrypt(rawData.getBytes(), this.SECRET.getBytes()).toString();
    log.info("Data was encrypted successfully {}",cipheredData);
    return cipheredData;
  }

  public String decrypt(String cipheredData) throws GeneralSecurityException {
    log.info("Decrypting data {}",cipheredData);
    final String rawData = this.aead.decrypt(cipheredData.getBytes(), this.SECRET.getBytes()).toString();
    log.info("Data was decrypted successfully {}",rawData);
    return rawData;
  }

}
