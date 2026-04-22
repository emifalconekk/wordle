package com.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.InputStream;

public class logic {

    public static final String RESET    = "\u001B[0m";
    public static final String VERDE    = "\u001B[32m";
    public static final String AMARILLO = "\u001B[33m";
    public static final String GRIS     = "\u001B[90m";

    public List<String> lawordslist = new ArrayList<String>();
    static protected String lawords = "/wordle-La.txt";
    public List<String> tawordslist = new ArrayList<String>();
    static protected String tawords = "/wordle-Ta.txt";

    public void loadWords(String path, List<String> list) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.out.println("No se encontró el archivo de palabras");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line.trim().toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> selectWord(List<String> list) {
        Random rand = new Random();
        int index = rand.nextInt(lawordslist.size());
        List<String> correct = new ArrayList<>(Arrays.asList(list.get(index).split("")));
        return correct;
    }

    public String gameLogic(List<String> correct, List<String> letters) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i).equals(correct.get(i))) {
                result.append(VERDE + letters.get(i) + RESET);
            } else if (correct.contains(letters.get(i))) {
                result.append(AMARILLO + letters.get(i) + RESET);
            } else {
                result.append(GRIS + letters.get(i) + RESET);
            }
        }
        return result.toString();
    }

    public boolean verifyWord(String word) {
        return lawordslist.contains(word) || tawordslist.contains(word);
    }

    public void gameOrchestrator() throws IOException {
        int cont = 0;
        List<String> word = selectWord(lawordslist);
        boolean flag = true;
        boolean notfinished = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (notfinished) {
            flag = true;
            System.out.println("Introduce a 5-letter word: ");
            List<String> letters = new ArrayList<>();
            while (flag) {
                String input = reader.readLine().toLowerCase();
                letters = new ArrayList<>(Arrays.asList(input.split("")));
                if (letters.size() != 5) {
                    System.out.println("The word has to have 5 letters");
                } else if (!verifyWord(input)) {
                    System.out.println("The word is not on the list");
                } else {
                    flag = false;
                    cont++;
                }
            }
            System.out.println(gameLogic(word, letters));
            if (word.equals(letters)) {
                System.out.println("Correct!");
                notfinished = false;
            }
            if (cont == 6) {
                System.out.println("You have no more tries");
                notfinished = false;
            }
        }
    }

    public Map<String, Integer> mostUsedLetters() {
        Map<String, Integer> puntos = new HashMap<>();
        for (char c = 'a'; c <= 'z'; c++) {
            puntos.put(String.valueOf(c), 0);
        }
        for (String word : lawordslist) {
            for (String letra : word.split("")) {
                puntos.merge(letra, 1, Integer::sum);
            }
        }
        return puntos.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    public String bestWords(int a) {
        Map<String, Integer> freqLetras = mostUsedLetters();
        Map<String, Integer> puntos = new HashMap<>();
        for (String word : tawordslist) {
            int total = 0;
            Set<String> letrasUnicas = new HashSet<>(Arrays.asList(word.split("")));
            for (String letra : letrasUnicas) {
                total += freqLetras.getOrDefault(letra, 0);
            }
            puntos.put(word, total);
        }
        List<String> claves = new ArrayList<>(
            puntos.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                )).keySet()
        );
        return claves.get(a);
    }

    public String bestComplement(String word, int position) {
        Map<String, Integer> freqLetras = mostUsedLetters();
        Set<String> letrasWord = new HashSet<>(Arrays.asList(word.split("")));
        Map<String, Integer> puntos = new HashMap<>();
        for (String w : tawordslist) {
            Set<String> letrasW = new HashSet<>(Arrays.asList(w.split("")));
            boolean overlap = false;
            for (String l : letrasW) {
                if (letrasWord.contains(l)) { overlap = true; break; }
            }
            if (overlap) continue;
            int total = 0;
            for (String letra : letrasW) {
                total += freqLetras.getOrDefault(letra, 0);
            }
            puntos.put(w, total);
        }
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(puntos.entrySet());
        lista.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        if (position > lista.size()) return "No hay suficientes palabras";
        return lista.get(position - 1).getKey();
    }
}