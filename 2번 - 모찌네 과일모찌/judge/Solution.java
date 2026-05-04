import java.util.*;

public class Solution {
    /**
     * 모찌네 과일모찌 가게 매출 조회
     *
     * @param N      판매 기록의 수 (1 <= N <= 100)
     * @param dates  각 판매의 날짜, "YYYY:MM:DD" 형식
     * @param names  각 판매의 상품 이름 (알파벳 소문자, 1~20자)
     * @param cnts   각 판매의 개수 (1 <= cnt <= 1,000)
     * @param Q      쿼리의 수 (1 <= Q <= 100)
     * @param starts 각 쿼리의 시작일 ("YYYY:MM:DD")
     * @param ends   각 쿼리의 종료일 ("YYYY:MM:DD")
     * @return result[i]는 i번째 쿼리의 답.
     *         해당 기간(starts[i] <= date <= ends[i])에 1개 이상 팔린 상품을
     *         이름 사전순으로 정렬한 "이름 개수" 형식 문자열의 배열.
     *         팔린 상품이 없으면 빈 배열.
     */
    public String[][] solution(int N, String[] dates, String[] names, int[] cnts,
                               int Q, String[] starts, String[] ends) {
        // 여기에 풀이를 작성하세요
        return new String[Q][0];
    }
}
