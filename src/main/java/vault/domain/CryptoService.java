package vault.domain;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.proto.KeyTemplate;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author claudioed on 2019-05-19. Project vault */
@Singleton
public class CryptoService {

  private static final Logger log = LoggerFactory.getLogger(CryptoService.class);

  private KeyTemplate keyTemplate = AeadKeyTemplates.AES256_GCM;

  private final KeysetHandle keysetHandle;

  private final Aead aead;

  private final String SECRET = "57d8ee35";

  public CryptoService() throws GeneralSecurityException {
    AeadConfig.register();
    this.keysetHandle = KeysetHandle.generateNew(keyTemplate);
    this.aead = AeadFactory.getPrimitive(keysetHandle);
  }

  public String encrypt(String rawData) throws GeneralSecurityException {
    log.info("Encrypting data {}",rawData);
    byte[] cipherText = this.aead.encrypt(rawData.getBytes(StandardCharsets.UTF_8), SECRET.getBytes());
    final String cipheredData = new String(Base64.getEncoder().encode(cipherText),StandardCharsets.UTF_8);
    log.info("Data was encrypted successfully {}",cipheredData);
    return cipheredData;
  }

  public String decrypt(String cipheredData) throws GeneralSecurityException {
    log.info("Decrypting data {}",cipheredData);
    String rawData = new String(aead.decrypt(Base64.getDecoder().decode(cipheredData), SECRET.getBytes()));
    log.info("Data was decrypted successfully {}",rawData);
    return rawData;
  }

}
