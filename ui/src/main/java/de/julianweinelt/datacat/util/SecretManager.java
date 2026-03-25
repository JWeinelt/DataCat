package de.julianweinelt.datacat.util;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecretManager {
    public static void save(String password) {
        try (Keyring keyring = Keyring.create()) {
            keyring.setPassword("datacat", "datacat", password);
        } catch (PasswordAccessException e) {
            log.error(e.getMessage(), e);
        } catch (BackendNotSupportedException e) {
            log.error("Backend not supported: {}", e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String loadPassword() {
        try (Keyring keyring = Keyring.create()) {
            return keyring.getPassword("datacat", "datacat");
        } catch (PasswordAccessException e) {
            log.error(e.getMessage(), e);
        } catch (BackendNotSupportedException e) {
            log.error("Backend not supported: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
