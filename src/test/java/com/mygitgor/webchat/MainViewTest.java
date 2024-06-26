package com.mygitgor.webchat;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringComponent
@UIScope
@SpringBootTest
public class MainViewTest {

    @Mock
    private VaadinSession vaadinSession;

    @Mock
    private Storage storage;

    @Mock
    private UI ui;

    @Mock
    private ListDataProvider<Storage.ChatMessage> dataProvider;

    @InjectMocks
    private MainView mainView;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        when(storage.getMessages()).thenReturn(new ConcurrentLinkedQueue<>());

        mainView = new MainView(storage);
        mainView.getGrid().setItems(dataProvider);

    }

    @Test
    public void testLogin(){
        VerticalLayout loginLayout = (VerticalLayout) mainView.getChildren()
                .filter(component -> component instanceof VerticalLayout).findFirst().get();

        TextField nameField = (TextField) loginLayout.getChildren()
                .filter(component -> component instanceof TextField).findFirst().get();

        Button loginButton = (Button) loginLayout.getChildren()
                .filter(component -> component instanceof Button).findFirst().get();

        assertTrue(loginLayout.isVisible());

        nameField.setValue("Test User");
        loginButton.click();

        assertFalse(loginLayout.isVisible());
        assertTrue(mainView.getChat().isVisible());
        verify(storage).addRecordJoined("Test User");
    }

    @Test
    public void testAddMessage(){
        mainView.setVisible(true);
        TextField messageField = (TextField) ((HorizontalLayout) mainView.getChat().getChildren()
                .filter(component -> component instanceof HorizontalLayout).findFirst().get())
                .getChildren().filter(component -> component instanceof TextField).findFirst().get();

        Button sendButton = (Button) ((HorizontalLayout) mainView.getChat().getChildren()
                .filter(component -> component instanceof HorizontalLayout).findFirst().get())
                .getChildren().filter(component -> component instanceof Button).findFirst().get();

        mainView.setUser("Test User");
        messageField.setValue("Hello User");
        sendButton.click();

        verify(storage).addRecord("Test User", "Hello User");
    }

    @Test
    public void testOnMessage(){
        Grid<Storage.ChatMessage> grid = mainView.getGrid();
        DataProvider<Storage.ChatMessage, ?> dataProvider = grid.getDataProvider();

        assertNotNull(dataProvider);

        Storage.ChatEvent chatEvent = new Storage.ChatEvent();
        mainView.onMessage(chatEvent);

        verify(vaadinSession).lock();
        verify(ui).access((Command) any(Runnable.class));
        verify(ui.getPage()).executeJs("$0._scrollToIndex($1)", grid, storage.size());
        verify(vaadinSession).unlock();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(ui).access((Command) runnableCaptor.capture());
        Runnable capturedRunnable = runnableCaptor.getValue();

        verify(dataProvider).refreshAll();
    }
}
