package com.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        logic game = new logic();
        game.loadWords(logic.lawords, game.lawordslist);
        game.loadWords(logic.tawords, game.tawordslist);

        String word1 = game.bestWords(5);
        String word2 = game.bestComplement(word1, 1);
        String word3 = game.bestComplement(word1 + word2, 1);
        String word4 = game.bestComplement(word1 + word2 + word3, 1);
        String word5 = game.bestComplement(word1 + word2 + word3 + word4, 1);

        System.out.println(word1);
        System.out.println(word2);
        System.out.println(word3);
        System.out.println(word4);
        System.out.println(word5);
    }
}