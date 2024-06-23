package com.mygitgor.webchat;

import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;


@Route("")
@Push
public class MainView extends VerticalLayout {
    private final Storage storage;
    private final Grid<Storage.ChatMessage> grid;
    private Registration registration;

    public MainView(Storage storage){
        this.storage = storage;

        grid = new Grid<>();
        grid.setItems(storage.getMessages());
        grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))))
                .setAutoWidth(true);

        TextField field = new TextField();
        add(
                new H3("Web Chat"),
                grid,
                new HorizontalLayout(){{
                    add(
                            field,
                            new Button("Send Message"){{
                        addClickListener(click -> {
                            storage.addRecord("", field.getValue());
                            field.clear();
                        });
                                addClickShortcut(Key.ENTER);
                            }}
                    );

                }}

        );

    }

    public void onMessage(Storage.ChatEvent event){
        if(getUI().isPresent()){
            UI ui = getUI().get();
            ui.getSession().lock();
            ui.access(() -> grid.getDataProvider().refreshAll());
            ui.getPage().executeJs("$0._scrollToIndex($1)", grid, storage.size());
            ui.getSession().unlock();
        }
    }

    private String renderRow(Storage.ChatMessage message) {
        return Processor.process(String.format("_User **%s** is just joined the chat!_", message.getMessage()));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        registration = storage.attachListener(this::onMessage);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        registration.remove();
    }
}
