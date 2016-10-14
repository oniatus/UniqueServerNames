package org.terasology.uniqueservernames.system;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIScreenLayer;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UILabel;
import org.terasology.rendering.nui.widgets.UIText;
import org.terasology.uniqueservernames.event.CheckValidNameEvent;
import org.terasology.uniqueservernames.event.CheckValidNameResponseEvent;
import org.terasology.uniqueservernames.event.RenameRequiredEvent;
import org.terasology.uniqueservernames.event.SubmitNameEvent;

//TODO: CLIENT: Works for all clients but hosting player gets a popup for each other player, REMOTE_CLIENT: Hosting player can not select a name
@RegisterSystem(RegisterMode.CLIENT)
public class ClientUniqueNameSystem extends BaseComponentSystem {


    @In
    private NUIManager nuiManager;

    private UIScreenLayer nameInputDialog;

    private UIText nameInput;

    private UIButton submitButton;

    private UILabel messageArea;

    @ReceiveEvent(components = ClientComponent.class)
    public void onAskForName(RenameRequiredEvent event, EntityRef entity) {
        ClientComponent clientComponent = entity.getComponent(ClientComponent.class);
        EntityRef clientInfo = clientComponent.clientInfo;
        String defaultName = clientInfo.getComponent(DisplayNameComponent.class).name;
        nuiManager.toggleScreen("uniqueServerNames:nameInputDialog");
        nameInputDialog = nuiManager.getScreen("uniqueServerNames:nameInputDialog");
        nameInput = nameInputDialog.find("nameInput", UIText.class);
        submitButton = nameInputDialog.find("submitButton", UIButton.class);

        messageArea = nameInputDialog.find("messageArea", UILabel.class);
        submitButton.setEnabled(false);
        submitButton.subscribe(x -> {
            submitName(entity);
            nuiManager.closeScreen(nameInputDialog);
        });
        nameInput.subscribe((String oldText, String newText) -> {
            validateName(newText, entity);
        });
        nameInput.setText(defaultName);
        //select the text
        nuiManager.setFocus(nameInput);
        nameInput.setCursorPosition(0);
        nameInput.setCursorPosition(nameInput.getText().length(), false);
    }

    private void submitName(EntityRef entity) {
        entity.send(new SubmitNameEvent(nameInput.getText()));

    }

    private void validateName(String newText, EntityRef entity) {
        CheckValidNameEvent checkValidNameEvent = new CheckValidNameEvent();
        checkValidNameEvent.setNameToCheck(newText);
        entity.send(checkValidNameEvent);
    }

    @ReceiveEvent
    public void onValidateResponse(CheckValidNameResponseEvent event, EntityRef entity) {
        if (nameInputDialog.isVisible()) {
            if (isRecent(event)) {
                boolean valid = event.isValid();
                submitButton.setEnabled(valid);
                messageArea.setText(String.format("The name '%s' is %s.", nameInput.getText(), valid ? "valid" : "not valid"));
            }
        }
    }

    private boolean isRecent(CheckValidNameResponseEvent event) {
        //the user may keep typing so we need to check if the event matches the latest input
        return event.getNameToCheck().equals(nameInput.getText());
    }
}
