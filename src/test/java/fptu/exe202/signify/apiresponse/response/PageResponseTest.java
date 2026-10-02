package fptu.exe202.signify.apiresponse.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void firstPageWithData() {
        PageResponse<String> response = PageResponse.of(List.of("a", "b"), 0, 10, 25);

        assertThat(response.getContent()).containsExactly("a", "b");
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(25);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isFalse();
        assertThat(response.isEmpty()).isFalse();
    }

    @Test
    void middlePage() {
        PageResponse<String> response = PageResponse.of(List.of("b"), 1, 10, 25);

        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isFalse();
        assertThat(response.getTotalPages()).isEqualTo(3);
    }

    @Test
    void lastPage() {
        PageResponse<String> response = PageResponse.of(List.of("c"), 2, 10, 25);

        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isTrue();
    }

    @Test
    void singlePage() {
        PageResponse<String> response = PageResponse.of(List.of("a"), 0, 10, 1);

        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
    }

    @Test
    void emptyPage() {
        PageResponse<String> response = PageResponse.empty(0, 10);

        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
        assertThat(response.isEmpty()).isTrue();
    }

    @Test
    void emptyContentCanHaveMatchingElementsWhenPageIsBeyondLast() {
        PageResponse<String> response = PageResponse.of(List.of(), 3, 10, 25);

        assertThat(response.getPage()).isEqualTo(3);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isTrue();
        assertThat(response.isEmpty()).isTrue();
    }

    @Test
    void divisibleTotalElements() {
        assertThat(PageResponse.of(List.of("a"), 1, 10, 20).getTotalPages()).isEqualTo(2);
    }

    @Test
    void nonDivisibleTotalElements() {
        assertThat(PageResponse.of(List.of("a"), 1, 10, 21).getTotalPages()).isEqualTo(3);
    }

    @Test
    void invalidArguments() {
        assertThatThrownBy(() -> PageResponse.of(List.of(), 0, 0, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageResponse.of(List.of(), 0, -1, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageResponse.of(List.of(), 0, 10, -1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageResponse.of(List.of(), -1, 10, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PageResponse.of(null, 0, 10, 0))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void largeTotalsDoNotOverflow() {
        PageResponse<String> response = PageResponse.of(List.of(), 0, 1, Long.MAX_VALUE);

        assertThat(response.getTotalPages()).isEqualTo(Long.MAX_VALUE);
        assertThat(PageResponse.of(List.of(), 0, 10, Long.MAX_VALUE).getTotalPages())
                .isEqualTo(922337203685477581L);
    }

    @Test
    void contentIsSnapshotAndCannotBeModified() {
        List<String> content = new ArrayList<>(List.of("a"));
        PageResponse<String> response = PageResponse.of(content, 0, 10, 1);
        content.add("b");

        assertThat(response.getContent()).containsExactly("a");
        assertThatThrownBy(() -> response.getContent().add("c"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void existingSuccessFactoriesKeepPageInGenericDataAndSerializeCorrectly() throws Exception {
        PageResponse<UserResponse> page = PageResponse.of(
                List.of(new UserResponse(1, "john")), 0, 10, 25);
        ApiResponse<PageResponse<UserResponse>> defaultMessage = ApiResponse.success(page);
        ApiResponse<PageResponse<UserResponse>> customMessage =
                ApiResponse.success("Users retrieved successfully", page);

        assertThat(defaultMessage.getData()).isSameAs(page);
        assertThat(defaultMessage.getMessage()).isEqualTo("Success");
        assertThat(customMessage.getData()).isSameAs(page);
        assertThat(customMessage.getMessage()).isEqualTo("Users retrieved successfully");

        JsonNode root = objectMapper.readTree(objectMapper.writeValueAsString(customMessage));
        assertThat(root.size()).isEqualTo(5);
        assertThat(root.get("success").asBoolean()).isTrue();
        assertThat(root.get("status").asInt()).isEqualTo(200);
        assertThat(root.get("message").asText()).isEqualTo("Users retrieved successfully");
        assertThat(root.get("timestamp").isTextual()).isTrue();
        JsonNode data = root.get("data");
        assertThat(data.size()).isEqualTo(8);
        assertThat(data.get("content").get(0).get("id").asInt()).isEqualTo(1);
        assertThat(data.get("content").get(0).get("username").asText()).isEqualTo("john");
        assertThat(data.get("page").asInt()).isZero();
        assertThat(data.get("size").asInt()).isEqualTo(10);
        assertThat(data.get("totalElements").asLong()).isEqualTo(25);
        assertThat(data.get("totalPages").asLong()).isEqualTo(3);
        assertThat(data.get("first").asBoolean()).isTrue();
        assertThat(data.get("last").asBoolean()).isFalse();
        assertThat(data.get("empty").asBoolean()).isFalse();
    }

    private record UserResponse(int id, String username) {
    }
}
