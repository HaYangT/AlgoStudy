import java.io.*;
import java.util.*;

public class Main {

    // ── 설정 ──
    static final String TEST_DIR = "tests";
    static final int TOTAL_TESTS = 20;
    static final long TIME_LIMIT_MS = 1000;  // 1초

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║              에베레스트산 휴게소 - 채점 시작                     ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  시간 제한: %d ms  |  테스트 케이스: %d개                        ║%n",
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
                int[] parsedN = new int[1];
                long[] parsedH = new long[1];
                int[][] edges;
                long[] queries;

                try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
                    StringTokenizer st = new StringTokenizer(br.readLine());
                    parsedN[0] = Integer.parseInt(st.nextToken());
                    parsedH[0] = Long.parseLong(st.nextToken());

                    int N = parsedN[0];
                    edges = new int[N - 1][3];
                    for (int i = 0; i < N - 1; i++) {
                        st = new StringTokenizer(br.readLine());
                        edges[i][0] = Integer.parseInt(st.nextToken());
                        edges[i][1] = Integer.parseInt(st.nextToken());
                        edges[i][2] = Integer.parseInt(st.nextToken());
                    }

                    int Q = Integer.parseInt(br.readLine().trim());
                    queries = new long[Q];
                    for (int i = 0; i < Q; i++) {
                        queries[i] = Long.parseLong(br.readLine().trim());
                    }
                }

                // ── 정답 로드 ──
                int[] expected;
                try (BufferedReader br = new BufferedReader(new FileReader(outputFile))) {
                    List<Integer> list = new ArrayList<>();
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty()) list.add(Integer.parseInt(line));
                    }
                    expected = list.stream().mapToInt(Integer::intValue).toArray();
                }

                // ── GC 후 메모리 측정 준비 ──
                Runtime rt = Runtime.getRuntime();
                rt.gc();
                Thread.sleep(50);
                long memBefore = rt.totalMemory() - rt.freeMemory();

                // ── 실행 및 시간 측정 ──
                long startTime = System.nanoTime();
                int[] result = sol.solution(parsedN[0], parsedH[0], edges, queries);
                long elapsed = (System.nanoTime() - startTime) / 1_000_000; // ms

                long memAfter = rt.totalMemory() - rt.freeMemory();
                long memUsed = Math.max(0, memAfter - memBefore) / 1024; // KB

                maxTime = Math.max(maxTime, elapsed);
                maxMemory = Math.max(maxMemory, memUsed);

                // ── 결과 비교 ──
                boolean timeLimitExceeded = elapsed > TIME_LIMIT_MS;
                boolean correct = Arrays.equals(result, expected);

                String status;
                if (timeLimitExceeded) {
                    status = "시간초과";
                    tle++;
                } else if (correct) {
                    status = "맞았습니다!";
                    passed++;
                } else {
                    status = "틀렸습니다..";
                    failed++;
                }

                System.out.printf("TC %02d | %s | %4d ms | %6d KB | N=%,d Q=%,d%n",
                        t, status, elapsed, memUsed, parsedN[0], queries.length);

                // 오답이면 첫 번째 불일치 출력
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
            System.out.println("모든 테스트 케이스 통과! 맞았습니다!!");
        }
    }

    static void printFirstMismatch(int[] expected, int[] result) {
        if (result == null) {
            System.out.println("         → 반환값이 null입니다.");
            return;
        }
        if (expected.length != result.length) {
            System.out.printf("         → 배열 길이 불일치: 예상 %d, 실제 %d%n",
                    expected.length, result.length);
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != result[i]) {
                System.out.printf("         → 쿼리 %d번째: 예상 %d, 실제 %d%n",
                        i + 1, expected[i], result[i]);
                return;
            }
        }
    }
}
