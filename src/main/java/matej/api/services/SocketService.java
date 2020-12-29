package matej.api.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

@Service
public class SocketService {

    private Map<String, String> userMap = new HashMap<>();

    public void addUUID(String username, String uuid) {
        userMap.put(username, uuid);
    }

    public String getUUIDFromUsername(String username) {
        String uuid = "";
        for (Entry<String, String> entry : userMap.entrySet()) {
            if (entry.getKey().equals(username)) {
                uuid = entry.getValue();
            }
        }
        return uuid;
    }
}