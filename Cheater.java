import java.util.*;

public class Cheater implements Adjuster {
  private int secretLen;
  private Random rand;
  private List<String> wordList; // all candidate words with the same length and same position pattern

  public Cheater() {
    secretLen = 0;
    rand = new Random();
    wordList = new ArrayList<>();
  }

  @Override
  public int chooseSecret(TreeMap<Integer, List<String>> dict) {
    // get a random length for secret
    int lo = dict.firstKey();
    int hi = dict.lastKey();
    int len = rand.nextInt(hi - lo) + lo;
    secretLen = dict.ceilingKey(len);

    // copy all the words of the same length as candidates
    wordList.addAll(dict.get(secretLen));
    // don't settle down a secret at this time

    return secretLen;
  }

  // Ituition:
  // if letter is not contained in current word list, no need to cheat
  // if return yes, 
  //    select a word and give back the positions
  //    word selection is decided by which position has a larger list
  //    remove all other words
  // if return no, remove all words containing this letter
  // decision is made by comparing which list is larger after deletion
  @Override
  public List<Integer> judge(char letter) {
    // position state -> a list of words follow this position patter
    // eg. letter == 'a', the position state of 'a' in "hangman" is 0...0100010
    HashMap<Integer, List<String>> wordsWithPosition = new HashMap<>();

    // keep track of the position state of the largest list
    int posWithLargestList = 0;
    int maxSize = 0;

    // iterate each word from current wordList, find the position pattern of it and group together
    for (int w = 0; w < wordList.size(); w++) {
      String word = wordList.get(w);
      int positionState = 0;

      for (int i = 0; i < word.length(); i++) {
        char l = word.charAt(i);
        if (l == letter) positionState |= 1 << i;
      }

      wordsWithPosition.putIfAbsent(positionState, new ArrayList<>());
      wordsWithPosition.get(positionState).add(word);

      int size = wordsWithPosition.get(positionState).size();
      // if find a larger list
      if (size > maxSize || size == maxSize && positionState == 0) {
        maxSize = size;
        posWithLargestList = positionState;
      }
    }

    List<Integer> res = new ArrayList<>();
    // update the wordList to the largest list
    wordList = wordsWithPosition.get(posWithLargestList);
    if (posWithLargestList == 0) {
      // number of words not containing this letter is the largest
      // this letter maybe even not in current wordList
      return res;
    }

    // it's not worthy to say this letter is wrong at this time
    String selectedSecret = wordList.get(0);
    for (int i = 0; i < selectedSecret.length(); i++) {
      char l = selectedSecret.charAt(i);
      if (l == letter) res.add(i);
    }

    return res;
  }

  @Override
  public String revealSecret() {
    if (wordList.size() == 0) return ""; // should never hit here
    return wordList.get(0);
  }
}