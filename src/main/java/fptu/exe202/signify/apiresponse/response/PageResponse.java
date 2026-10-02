package fptu.exe202.signify.apiresponse.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A page of already retrieved data with its pagination metadata.
 * Page numbers are zero-based. This class does not fetch or slice data.
 *
 * @param <T> the type of each item in the page
 */
@JsonPropertyOrder({"content", "page", "size", "totalElements", "totalPages", "first", "last", "empty"})
public final class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final long totalPages;
    private final boolean first;
    private final boolean last;
    private final boolean empty;

    private PageResponse(List<T> content, int page, int size, long totalElements) {
        Objects.requireNonNull(content, "content must not be null");
        if (page < 0) {
            throw new IllegalArgumentException("page must be zero or greater");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than zero");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements must be zero or greater");
        }

        this.content = Collections.unmodifiableList(new ArrayList<>(content));
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalElements / size + (totalElements % size == 0 ? 0 : 1);
        this.first = page == 0;
        this.last = totalPages == 0 || page >= totalPages - 1;
        this.empty = content.isEmpty();
    }

    /** Creates a response from content that has already been paginated. */
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        return new PageResponse<>(content, page, size, totalElements);
    }

    /** Creates an empty page with no matching elements. */
    public static <T> PageResponse<T> empty(int page, int size) {
        return new PageResponse<>(List.of(), page, size, 0);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public boolean isEmpty() {
        return empty;
    }
}
