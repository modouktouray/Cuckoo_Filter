import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CuckooFilter {
    private String[][] buckets;
    private int bucketSize;
    private int bucketCount;
    private int maxKicks;
    private Random random = new Random();

    public CuckooFilter(int bucketSize, int bucketCount, int maxKicks) {
        this.bucketSize = bucketSize;
        this.bucketCount = bucketCount;
        this.maxKicks = maxKicks;
        this.buckets = new String[bucketCount][bucketSize];
        for (int i = 0; i < bucketCount; i++) {
            for (int j = 0; j < bucketSize; j++) {
                buckets[i][j] = null; // null indicates an empty slot
            }
        }
    }

    private int hashFunction(String key) {
        return Math.abs(key.hashCode()) % bucketCount;
    }

    public boolean add(String key) {
        int primaryIndex = hashFunction(key);
        if (placeKeyInBucket(key, primaryIndex)) return true;

        int secondaryIndex = (primaryIndex + 1) % bucketCount;
        if (placeKeyInBucket(key, secondaryIndex)) return true;

        int currentIndex = random.nextBoolean() ? primaryIndex : secondaryIndex;
        for (int i = 0; i < maxKicks; i++) {
            key = kickOutKey(key, currentIndex);
            if (key == null) return true;
            currentIndex = currentIndex == primaryIndex ? secondaryIndex : primaryIndex;
        }

        return false;
    }

    public boolean contains(String key) {
        return findKeyInBucket(key, hashFunction(key)) || findKeyInBucket(key, (hashFunction(key) + 1) % bucketCount);
    }

    private boolean placeKeyInBucket(String key, int bucketIndex) {
        for (int i = 0; i < bucketSize; i++) {
            if (buckets[bucketIndex][i] == null) {
                buckets[bucketIndex][i] = key;
                return true;
            }
        }
        return false;
    }

    private String kickOutKey(String key, int bucketIndex) {
        int slot = random.nextInt(bucketSize);
        String kickedOutKey = buckets[bucketIndex][slot];
        buckets[bucketIndex][slot] = key;
        return kickedOutKey;
    }

    private boolean findKeyInBucket(String key, int bucketIndex) {
        for (int i = 0; i < bucketSize; i++) {
            if (key.equals(buckets[bucketIndex][i])) {
                return true;
            }
        }
        return false;
    }

    public List<String> getAllKeys() {
        List<String> keys = new ArrayList<>();
        for (String[] bucket : buckets) {
            for (String key : bucket) {
                if (key != null) {
                    keys.add(key);
                }
            }
        }
        return keys;
    }
}