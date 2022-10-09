import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class PageTest {
    @Test
    void openFile() {
        Page page = Mockito.mock(Page.class);
        page.open();
        Mockito.verify(page).open();
    }

    @Test
    void savetest() {
        Page page = Mockito.mock(Page.class);
        page.fileItem_save();
        Mockito.verify(page).fileItem_save();
    }

    @Test
    void searchtest(){
        Page page = Mockito.mock(Page.class);
        page.search();
        Mockito.verify(page).search();
    }
}