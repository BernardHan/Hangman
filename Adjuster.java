import java.util.*;

interface Adjuster {
  // judge if letter exists in secret word
  List<Integer> judge(char letter);
  // get a random word length, and choose a random secret word
  int chooseSecret(TreeMap<Integer, List<String>> dict);
  // show the secret word
  String revealSecret();
}