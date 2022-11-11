package top.kaluna.modbusTcp.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * @author Yuery
 * @date 2022/2/25/0025 - 11:16
 */
public class PageReq {
    @NotNull(message = "[页码]不能为空")
    private int page;

    @NotNull(message = "[每页条数]不能为空")
    @Max(value = 1000, message = "[每页条数]不能超过1000")
    private int pageSize;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageReq{");
        sb.append("page=").append(page);
        sb.append(", pageSize=").append(pageSize);
        sb.append('}');
        return sb.toString();
    }
}
