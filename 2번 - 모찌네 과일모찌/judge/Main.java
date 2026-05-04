import java.io.*;
import java.util.*;

public class Main {

    // ── 설정 ──
    static final String TEST_DIR = "tests";
    static final int TOTAL_TESTS = 11;
    static final long TIME_LIMIT_MS = 1000;  // 1초

    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(null, Main::runJudge, "judge", 1 << 26);
        thread.start();
        thread.join();
    }

    static void runJudge() {
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║              모찌네 과일모찌 - 채점 시작                            ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  시간 제한: %d ms  |  테스트 케이스: %d개                         ║%n",
                TIME_LIMIT_MS, TOTAL_TESTS);
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();

        Solution sol = new Solution();

        int passed = 0;
        int failed = 0;
        int tle = 0;
        int error = 0;
        long maxTime = 0;
        long maxMemory = 0;

        for (int t = 1; t <= TOTAL_TESTS; t++) {
            String inputFile = String.format("%s/input_%02d.txt", TEST_DIR, t);
            String outputFile = String.format("%s/output_%02d.txt", TEST_DIR, t);

            try {
                // ── 입력 파싱 ──
                int N, Q;
                String[] dates, names, starts, ends;
                int[] cnts;

                try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                    N = Integer.parseInt(br.readLine().trim());
                    dates = new String[N];
                    names = new String[N];
                    cnts = new int[N];
                    for (int i = 0; i < N; i++) {
                        StringTokenizer st = new StringTokenizer(br.readLine());
                        dates[i] = st.nextToken();
                        names[i] = st.nextToken();
                        cnts[i] = Integer.parseInt(st.nextToken());
                    }

                    Q = Integer.parseInt(br.readLine().trim());
                    starts = new String[Q];
                    ends = new String[Q];
                    for (int i = 0; i < Q; i++) {
                        StringTokenizer st = new StringTokenizer(br.readLine());
                        starts[i] = st.nextToken();
                        ends[i] = st.nextToken();
                    }
                }

                // ── 정답 로드 ──
                // 형식: 쿼리마다 첫 줄에 K (해당 쿼리의 결과 개수), 다음 K줄에 "이름 개수"
                String[][] expected = new String[Q][];
                try (BufferedReader br = new BufferedReader(new FileReader(outputFile))) {
                    for (int q = 0; q < Q; q++) {
                        int K = Integer.parseInt(br.readLine().trim());
                        expected[q] = new String[K];
                        for (int i = 0; i < K; i++) {
                            expected[q][i] = br.readLine().trim();
                        }
                    }
                }

                // ── GC 후 메모리 측정 준비 ──
                Runtime rt = Runtime.getRuntime();
                rt.gc();
                Thread.sleep(50);
                long memBefore = rt.totalMemory() - rt.freeMemory();

                // ── 실행 및 시간 측정 ──
                long startTime = System.nanoTime();
                String[][] result = sol.solution(N, dates, names, cnts, Q, starts, ends);
                long elapsed = (System.nanoTime() - startTime) / 1_000_000; // ms

                long memAfter = rt.totalMemory() - rt.freeMemory();
                long memUsed = Math.max(0, memAfter - memBefore) / 1024; // KB

                maxTime = Math.max(maxTime, elapsed);
                maxMemory = Math.max(maxMemory, memUsed);

                // ── 결과 비교 ──
                boolean timeLimitExceeded = elapsed > TIME_LIMIT_MS;
                boolean correct = deepEquals(expected, result);

                String status;
                if (timeLimitExceeded) {
                    status = "시간 초과!";
                    tle++;
                } else if (correct) {
                    status = "맞았습니다!";
                    passed++;
                } else {
                    status = "틀렸습니다!";
                    failed++;
                }

                System.out.printf("TC %02d | %s | %4d ms | %6d KB | N=%,d Q=%,d%n",
                        t, status, elapsed, memUsed, N, Q);

                if (!correct && !timeLimitExceeded) {
                    printFirstMismatch(expected, result);
                }

            } catch (Exception e) {
                error++;
                System.out.printf("TC %02d | 런타임 에러 | %s%n", t, e.getMessage());
            }
        }

        // ── 요약 ──
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════════");
        System.out.printf("  결과: %d / %d PASSED%n", passed, TOTAL_TESTS);
        if (failed > 0) System.out.printf("  오답: %d개%n", failed);
        if (tle > 0)    System.out.printf("  시간 초과: %d개%n", tle);
        if (error > 0)  System.out.printf("  에러: %d개%n", error);
        System.out.printf("  최대 실행 시간: %d ms%n", maxTime);
        System.out.printf("  최대 메모리 사용: %,d KB%n", maxMemory);
        System.out.println("════════════════════════════════════════════════════════════════════");

        if (passed == TOTAL_TESTS) {
            System.out.println("모든 테스트케이스를 통과했습니다.");
        }
    }

    static boolean deepEquals(String[][] expected, String[][] result) {
        if (result == null || expected.length != result.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (result[i] == null || expected[i].length != result[i].length) return false;
            for (int j = 0; j < expected[i].length; j++) {
                if (!expected[i][j].equals(result[i][j])) return false;
            }
        }
        return true;
    }

    static void printFirstMismatch(String[][] expected, String[][] result) {
        if (result == null) {
            System.out.println("         → 반환값이 null입니다.");
            return;
        }
        if (expected.length != result.length) {
            System.out.printf("         → 쿼리 개수 불일치: 예상 %d, 실제 %d%n",
                    expected.length, result.length);
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (result[i] == null) {
                System.out.printf("         → 쿼리 %d: 결과 배열이 null입니다.%n", i + 1);
                return;
            }
            if (expected[i].length != result[i].length) {
                System.out.printf("         → 쿼리 %d: 항목 수 불일치 (예상 %d, 실제 %d)%n",
                        i + 1, expected[i].length, result[i].length);
                System.out.printf("           예상: %s%n", Arrays.toString(expected[i]));
                System.out.printf("           실제: %s%n", Arrays.toString(result[i]));
                return;
            }
            for (int j = 0; j < expected[i].length; j++) {
                if (!expected[i][j].equals(result[i][j])) {
                    System.out.printf("         → 쿼리 %d, %d번째 항목 불일치%n", i + 1, j + 1);
                    System.out.printf("           예상: \"%s\"%n", expected[i][j]);
                    System.out.printf("           실제: \"%s\"%n", result[i][j]);
                    return;
                }
            }
        }
    }
}
