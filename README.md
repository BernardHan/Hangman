# How to run Hangman
- javac Hangman.java
- java Hangman
- Enjoy!

# Components
- Hangman: the game manager, responsible for displaying game info, communications between computer and the player
- Adjuster: an interface following strategy pattern, implemetations are responsible for choosing a secret and judging if the letter guessed exists in the secret or not
- StandardAdjuster: a standard computer who follows the game rule
- Cheater: a cunning cheater who trick the player and try best to make the player lose!

# Limitations
- The dict.txt cannot have any words with length larger than 32

# Ituition of the Cheater
- When choosing a secret, cheater get a random length for secret, but does not settle down for a secret, and keeps a word list of the settled length
- When judging on a guess, it follows this procedure:
  - if letter is not contained in current word list, no need to cheat
  - if return yes, 
      - select a word and give back the positions
      - word selection is decided by which position has a larger list
      - keep the largest list
  - if return no, remove all words containing this letter
  - decision between yes or no is made by comparing which list is larger after deletion

# Potential Improvement for Cheater
- When keeping the largest list for yes, the list may contain anagrams, or multiple words have the same position pattern for a certain letter, which may reduce the future winning chance. We can find a way to find the largest list with least anagrams and same position patterns.
- When the list from yes is larger than no, and player only has one chance left, it's obviously to say no by human ituition. To achieve this, we can setup a threshold. If the list from yes is larger than no by this threshold, then adjuster can say yes. The threshold can be determined by simulating different number for thousands of time and choose the best.

# Potential other Implementation of Cheater
- Always say no as long as the aftermath word list is not empty
- Assume the player is also another computer and has the same dictionary, then the player always plays to the best. When our cheater judges, simulates with DFS of a decision tree made by both cheater and the player, where cheater tries to keep as many words as possible and player tries to reduce as many words as possible. When DFS is done, cheater choose the path with the most words left.