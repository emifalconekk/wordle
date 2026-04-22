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

    public List<String> lawordslist = new ArrayList<>();
    static protected String lawords = "/wordle-La.txt";
    public List<String> tawordslist = new ArrayList<>();
    static protected String tawords = "/wordle-Ta.txt";

    public List<String> candidates = new ArrayList<>();

    public void loadWords(String path, List<String> list) {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) { System.out.println("No se encontró el archivo"); return; }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) list.add(line.trim().toLowerCase());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<String> selectWord(List<String> list) {
        Random rand = new Random();
        int index = rand.nextInt(lawordslist.size());
        return new ArrayList<>(Arrays.asList(list.get(index).split("")));
    }

    public String gameLogic(List<String> correct, List<String> letters) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < letters.size(); i++) {
            if (letters.get(i).equals(correct.get(i)))
                result.append(VERDE + letters.get(i) + RESET);
            else if (correct.contains(letters.get(i)))
                result.append(AMARILLO + letters.get(i) + RESET);
            else
                result.append(GRIS + letters.get(i) + RESET);
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
                if (letters.size() != 5) System.out.println("The word has to have 5 letters");
                else if (!verifyWord(input)) System.out.println("The word is not on the list");
                else { flag = false; cont++; }
            }
            System.out.println(gameLogic(word, letters));
            if (word.equals(letters)) { System.out.println("Correct!"); notfinished = false; }
            if (cont == 6) { System.out.println("You have no more tries"); notfinished = false; }
        }
    }

    public Map<String, Integer> mostUsedLetters() {
        Map<String, Integer> puntos = new HashMap<>();
        for (char c = 'a'; c <= 'z'; c++) puntos.put(String.valueOf(c), 0);
        for (String word : lawordslist)
            for (String letra : word.split(""))
                puntos.merge(letra, 1, Integer::sum);
        return puntos.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public String bestWords(int a) {
        Map<String, Integer> freqLetras = mostUsedLetters();
        Map<String, Integer> puntos = new HashMap<>();
        for (String word : tawordslist) {
            int total = 0;
            for (String letra : new HashSet<>(Arrays.asList(word.split(""))))
                total += freqLetras.getOrDefault(letra, 0);
            puntos.put(word, total);
        }
        List<String> claves = new ArrayList<>(
            puntos.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                .keySet()
        );
        return claves.get(a);
    }

    public String bestComplement(String word, int position) {
        Map<String, Integer> freqLetras = mostUsedLetters();
        Set<String> letrasWord = new HashSet<>(Arrays.asList(word.split("")));
        Map<String, Integer> puntos = new HashMap<>();
        for (String w : tawordslist) {
            Set<String> letrasW = new HashSet<>(Arrays.asList(w.split("")));
            boolean overlap = letrasW.stream().anyMatch(letrasWord::contains);
            if (overlap) continue;
            int total = 0;
            for (String letra : letrasW) total += freqLetras.getOrDefault(letra, 0);
            puntos.put(w, total);
        }
        List<Map.Entry<String, Integer>> lista = new ArrayList<>(puntos.entrySet());
        lista.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        if (position > lista.size()) return "No hay suficientes palabras";
        return lista.get(position - 1).getKey();
    }

    public String bestWordsPositional(int a) {
        // Frecuencia por posición
        List<Map<String, Integer>> posFreq = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Integer> freq = new HashMap<>();
            for (char c = 'a'; c <= 'z'; c++) freq.put(String.valueOf(c), 0);
            posFreq.add(freq);
        }
        for (String word : lawordslist) {
            String[] letters = word.split("");
            for (int i = 0; i < letters.length; i++)
                posFreq.get(i).merge(letters[i], 1, Integer::sum);
        }

        // Puntuar cada palabra
        Map<String, Integer> puntos = new HashMap<>();
        for (String word : tawordslist) {
            String[] letters = word.split("");
            Set<String> vistas = new HashSet<>();
            int total = 0;
            for (int i = 0; i < letters.length; i++) {
                if (!vistas.contains(letters[i])) {
                    total += posFreq.get(i).getOrDefault(letters[i], 0);
                    vistas.add(letters[i]);
                }
            }
            puntos.put(word, total);
        }

        List<String> claves = new ArrayList<>(
            puntos.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                .keySet()
        );
        return claves.get(a);
    }

    // ─────────────────────────────────────────
    //  SOLVER
    // ─────────────────────────────────────────

    public void initSolver() {
        candidates = new ArrayList<>(lawordslist);
    }

    public void filterCandidates(String guess, String feedback) {
        String[] g = guess.split("");
        String[] fb = feedback.toUpperCase().split("");
        candidates.removeIf(candidate -> !matchesFeedback(candidate, g, fb));
    }

    private boolean matchesFeedback(String candidate, String[] guess, String[] feedback) {
        String[] c = candidate.split("");
        for (int i = 0; i < 5; i++) {
            switch (feedback[i]) {
                case "G":
                    if (!c[i].equals(guess[i])) return false;
                    break;
                case "Y":
                    if (c[i].equals(guess[i])) return false;
                    if (!candidate.contains(guess[i])) return false;
                    break;
                case "X":
    final int iCopy = i; // ← variable final para usar en el lambda
    long confirmedCount = 0;
    for (int j = 0; j < 5; j++)
        if (guess[j].equals(guess[iCopy]) && (feedback[j].equals("G") || feedback[j].equals("Y")))
            confirmedCount++;
    long candCount = Arrays.stream(c).filter(l -> l.equals(guess[iCopy])).count();
    if (candCount != confirmedCount) return false;
    break;
            }
        }
        return true;
    }

    public String bestGuess() {
        if (candidates.size() == 1) return candidates.get(0);

        // Frecuencia posicional sobre candidatos restantes
        List<Map<String, Integer>> posFreq = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Integer> freq = new HashMap<>();
            for (char c = 'a'; c <= 'z'; c++) freq.put(String.valueOf(c), 0);
            posFreq.add(freq);
        }
        for (String word : candidates) {
            String[] letters = word.split("");
            for (int i = 0; i < letters.length; i++)
                posFreq.get(i).merge(letters[i], 1, Integer::sum);
        }

        String best = null;
        int bestScore = -1;
        for (String word : candidates) {
            String[] letters = word.split("");
            Set<String> vistas = new HashSet<>();
            int score = 0;
            for (int i = 0; i < letters.length; i++) {
                if (!vistas.contains(letters[i])) {
                    score += posFreq.get(i).getOrDefault(letters[i], 0);
                    vistas.add(letters[i]);
                }
            }
            if (score > bestScore) { bestScore = score; best = word; }
        }
        return best;
    }

    public void solverOrchestrator() throws IOException {
        initSolver();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("╔══════════════════════════════╗");
        System.out.println("║       WORDLE  SOLVER         ║");
        System.out.println("╠══════════════════════════════╣");
        System.out.println("║ G = verde  (letra y posición)║");
        System.out.println("║ Y = amarillo (letra, no pos) ║");
        System.out.println("║ X = gris   (no está)         ║");
        System.out.println("╚══════════════════════════════╝");

        String guess = bestWordsPositional(0);

        for (int attempt = 1; attempt <= 6; attempt++) {
            System.out.println("\n[ Intento " + attempt + "/6 ]");
            System.out.println("Candidatos restantes: " + candidates.size());
            System.out.println("▶ Probá: " + guess.toUpperCase());

            System.out.print("Feedback (ej: GXYYX) o GGGGG si ganaste: ");
            String feedback = reader.readLine().trim().toUpperCase();

            if (feedback.equals("GGGGG")) {
                System.out.println("\n✅ ¡Resuelto en " + attempt + " intento" + (attempt > 1 ? "s" : "") + "!");
                return;
            }

            if (feedback.length() != 5 || !feedback.matches("[GYX]{5}")) {
                System.out.println("Feedback inválido. Usá solo G, Y, X (5 caracteres).");
                attempt--;
                continue;
            }

            filterCandidates(guess, feedback);

            if (candidates.isEmpty()) {
                System.out.println("⚠ No quedan candidatos. Revisá el feedback ingresado.");
                return;
            }

            if (candidates.size() <= 10)
                System.out.println("Palabras posibles: " + candidates);

            guess = bestGuess();
        }

        System.out.println("\n❌ No se resolvió en 6 intentos. Candidatos restantes: " + candidates);
    }
}