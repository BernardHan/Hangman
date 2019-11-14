import java.io.*;
import java.util.*;

/**
 * This is a game manager, responsible to display game info,
 * and communications between player and computer
 */
public class Hangman {
  // two types of adjusters
  enum ADJUSTER {
    STANDARD, ADVANCED;
  }

  // constants section, for displaying game info
  private final static String YOU_GUESS = "Your Guess:       ";
  private final static String MISS_HISTORY = "Miss History:     ";
  private final static String CURRENT_STATE = "Current State:    ";
  private final static String CHANCE_LEFT = "Chances Left:     ";
  private final static String EMPTY = "_";
  private final static String SPLITTER = "----------------------------------";
  private final static int CHANCES = 6;

  private String currentState; // current word guessed state
  private String missHistory; // missed history
  private boolean gameover;
  private int chances;
  private TreeMap<Integer, List<String>> dict; // a dictionary containing all words
  private Adjuster adjuster; // the computer player chooses to play against
  private Scanner in;

  public Hangman() {
    dict = new TreeMap<>();
    in = new Scanner(System.in);
    try {
      readDict();
    } catch (Exception err) {
      err.printStackTrace();
      return;
    }
    initialize();
  }

  // prompt for user input to select which computer to play against
  private int selectDifficulty() {
    System.out.println("Please select a difficulty (type a number):");
    System.out.println("0. Standard");
    System.out.println("1. Advanced");
    System.out.print("Your selection: ");

    boolean validInput = false;
    String difficultyStr;
    int difficulty = -1;

    // user input may be invalid, so prompt until receiving valid input
    while (!validInput) {
      difficultyStr = in.nextLine();
      try {
        difficulty = Integer.parseInt(difficultyStr);
        if (difficulty >= ADJUSTER.values().length) {
          throw new Exception();
        }
        validInput = true;
      } catch (Exception err) {
        System.out.print("Please select a valid difficulty: ");
      }
    }

    return difficulty;
  }

  // reset the game
  private void initialize() {
    currentState = "";
    chances = CHANCES;
    missHistory = "";
    gameover = false;

    int difficulty = selectDifficulty();
    
    if (difficulty == ADJUSTER.STANDARD.ordinal()) {
      adjuster = new StandardAdjuster();
    } else if (difficulty == ADJUSTER.ADVANCED.ordinal()) {
      adjuster = new Cheater();
    } else {
      System.out.println("Invalid adjuster");
      System.exit(0);
    }
  }

  private void readDict() throws Exception {
    BufferedReader br = new BufferedReader(new FileReader(new File("./dict.txt")));
    String line;

    while ((line = br.readLine()) != null) {
      dict.putIfAbsent(line.length(), new ArrayList<>());
      dict.get(line.length()).add(line);
    }

    br.close();
  }

  private int chooseSecret() {
    int secretLen = adjuster.chooseSecret(dict);

    // initial word state should be all dashes
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < secretLen; i++) {
      sb.append(EMPTY).append(" ");
    }
    currentState = sb.toString();

    return secretLen;
  }

  private char getPlayerGuess() {
    System.out.print(YOU_GUESS);
    boolean validInput = false;
    String guessStr = "";

    // user input may be invalid, so prompt until receiving valid input
    while (!validInput) {
      guessStr = in.nextLine();
      // only accept a single letter
      if (guessStr.length() != 1 || !Character.isLetter(guessStr.charAt(0))) {
        System.out.print("Please type a single letter: ");
        continue;
      }
      validInput = true;
    }

    // make letter lowercase
    return Character.toLowerCase(guessStr.charAt(0));
  }

  private void isGameover(int correct, int secretLen) {
    if (chances == 0) {
      gameover = true;
      System.out.println("You Lose!");
      System.out.println("Correct Answer:   " + adjuster.revealSecret());
    } else if (correct == secretLen) {
      gameover = true;
      System.out.println("You Win!");
    }
  }

  // game tells adjuster to judge, and then decide if game is over
  private int judge(char guess) {
    int correct = 0; // number of correct positions the player scored
    List<Integer> pos = adjuster.judge(guess); // positions of this guessed letter are in the secret word
    
    if (pos.size() == 0) {
      // if the guess is wrong
      chances--;
      if (missHistory.length() == 0) missHistory += guess;
      else missHistory += ", " + guess;
    } else {
      // if the guess is correct, change the word state
      char[] state = currentState.toCharArray();
      for (Integer replace : pos) {
        state[replace * 2] = guess; // each dash comes with a space, so times 2 to get the right dash
      }

      currentState = new String(state);
      correct += pos.size();
    }

    return correct;
  }

  // ask if player wants to play again
  private void replay() {
    System.out.println(SPLITTER);
    System.out.print("Play again? [y|n]: ");
    String answer = "";
    boolean validInput = false;

    while (!validInput) {
      answer = in.nextLine();
      if (!answer.equals("y") && !answer.equals("n")) {
        System.out.print("Please type either 'y' or 'n': ");
        continue;
      }
      validInput = true;
    }

    if (answer.equals("y")) {
      initialize();
      play();
    } else {
      System.out.println("Thank you for playing!");
      System.exit(0);
    }
  }

  public void play() {
    int correct = 0;
    int secretLen = chooseSecret();

    while (!gameover) {
      // display current game info
      System.out.println(SPLITTER);
      System.out.println(CHANCE_LEFT + chances);
      System.out.println(CURRENT_STATE + currentState);
      System.out.println(MISS_HISTORY + missHistory);
      // get guess from player and judge
      char guess = getPlayerGuess();
      correct += judge(guess);

      isGameover(correct, secretLen);
    }

    // when current game loop is terminated, ask player if want to play again
    replay();
  }

  public static void main(String[] args) {
    Hangman hangman = new Hangman();
    hangman.play();
  }
}