import java.util.*;

public class StandardAdjuster implements Adjuster {
  private int secretLen; // length of chosen secret word
  private String secret;
  private Random rand;
  HashMap<Character, List<Integer>> letters; // keep track of what letter -> positions pair left

  public StandardAdjuster() {
    secretLen = 0;
    rand = new Random();
    secret = "";
    letters = new HashMap<>();
  }

  @Override
  public int chooseSecret(TreeMap<Integer, List<String>> dict) {
    // get a random secret length
    int lo = dict.firstKey();
    int hi = dict.lastKey();
    int len = rand.nextInt(hi - lo) + lo;
    secretLen = dict.ceilingKey(len);

    // get a random secret based on the length
    List<String> words = dict.get(secretLen);
    secret = words.get(rand.nextInt(words.size()));

    for (int i = 0; i < secret.length(); i++) {
      char letter = secret.charAt(i);
      letters.putIfAbsent(letter, new ArrayList<>());
      letters.get(letter).add(i);
    }

    return secretLen;
  }

  @Override
  public List<Integer> judge(char letter) {
    // if letter exists in the secret, returns the positions of this letter in the secret
    if (letters.containsKey(letter)) {
      List<Integer> pos = letters.get(letter);
      letters.remove(letter);
      return pos;
    }

    return new ArrayList<>();
  }

  @Override
  public String revealSecret() {
    return secret;
  }
}