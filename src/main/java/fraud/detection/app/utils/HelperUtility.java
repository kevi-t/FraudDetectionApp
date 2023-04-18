package fraud.detection.app.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fraud.detection.app.models.Account;
import fraud.detection.app.models.User;
import fraud.detection.app.repositories.AccountRepository;
import fraud.detection.app.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
@Slf4j
@Service
public class HelperUtility {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    public HelperUtility(UserRepository userRepository, PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    /**
     * @param value the value to be converted to a base64 string
     * @return returns base64String
     */
    public static String toBase64String(String value) {
        byte[] data = value.getBytes(StandardCharsets.ISO_8859_1);
        return Base64.getEncoder().encodeToString(data);
    }
    public static String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException exception) {
            return null;
        }
    }
    public static String getTransactionUniqueNumber() {
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                .build();
        String transactionNumber=stringGenerator.generate(12).toUpperCase();
        log.info(String.format("Transaction Number: %s", transactionNumber));
        return transactionNumber;
    }
    public static String getStkPushPassword(String shortCode, String passKey, String timestamp) {
        String concatenatedString = String.format("%s%s%s", shortCode, passKey, timestamp);
        return toBase64String(concatenatedString);
    }

    public static String getTransactionTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }



    public Boolean checkPin(String pin,String account){
        User user=userRepository.findUserBymobileNumber(account);
        String dbPin= user.getPin();
        if (passwordEncoder.matches(pin, dbPin)){
            return true;
        }
        else {
            return false;
        }
    }
    public Boolean checkAccount(String account){
        if(accountRepository.findByAccountNumber(account)==null){
            return true;
        }
        else {
            return false;
        }

    }
    public Boolean checkAccountBalance(String accountNo,double transactionAmount){
     Account account= accountRepository.findByAccountNumber(accountNo);
     if (account.getAccountBalance()>transactionAmount){
         return true;
     }else {
         return false;
     }

    }

    //Generating unique reference code
    public static String referenceCodeGenerator(){
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString().toUpperCase().substring(0, 10).replaceAll("-", "A");
        String referenceCode = "TUCN" + randomUUIDString;
        return referenceCode;
    }


    @SneakyThrows
    public static String getSecurityCredentials(String initiatorPassword) {

        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] input = initiatorPassword.getBytes(StandardCharsets.UTF_8);

            Resource resource = new ClassPathResource("cert.cer");
            InputStream inputStream = resource.getInputStream();

            FileInputStream fin = new FileInputStream(resource.getFile());
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(fin);
            PublicKey pk = certificate.getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, pk);

            byte[] cipherText = cipher.doFinal(input);
            String cipherText2 = cipherText.toString();
            // Convert the resulting encrypted byte array into a string using base64 encoding
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String encryptedPassword = passwordEncoder.encode(cipherText2).trim();

            return encryptedPassword;
        } catch (NoSuchAlgorithmException | CertificateException | InvalidKeyException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | FileNotFoundException e) {
            log.error(String.format("Error generating security credentials ->%s", e.getLocalizedMessage()));
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }



    public static String checkPhoneNumber(String phoneNumber) {
        if (phoneNumber.matches("^((\\+)?254|0|01)\\d{8}$")) {
            if (phoneNumber.startsWith("+254")) {
                return phoneNumber.substring(1);
            } else if (phoneNumber.startsWith("01")) {
                return "254" + phoneNumber.substring(2);
            } else if (phoneNumber.startsWith("07")) {
                return "254" + phoneNumber.substring(2);
            } else {
                return phoneNumber;
            }
        } else {
            return "Invalid phone number";
        }
    }




}


