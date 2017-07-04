package ThreadsEx;

public class Counter implements Runnable {

    String[] words;

    public Counter(String[] words) {
        this.words = words;
    }

    private void Count(){
        for (String word : words) {
            if (Main.result.containsKey(word)) {
                Main.result.replace(word, Main.result.get(word) + 1);
            }
            else Main.result.put(word, 1l);
        }
    }

    public void run() {
        Count();
    }
}
