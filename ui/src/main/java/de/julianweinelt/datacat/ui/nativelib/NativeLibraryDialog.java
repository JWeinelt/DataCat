package de.julianweinelt.datacat.ui.nativelib;

import de.julianweinelt.datacat.DataCat;
import de.julianweinelt.datacat.dbx.api.plugins.DbxPlugin;
import de.julianweinelt.datacat.dbx.api.ui.ResponseDialog;
import de.julianweinelt.datacat.ui.BenchUI;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.julianweinelt.datacat.dbx.util.LanguageManager.translate;

public class NativeLibraryDialog extends JDialog implements ResponseDialog {
    @Getter
    private int result = -1;

    private final CompletableFuture<Integer> responseFuture = new CompletableFuture<>();
    private final CompletableFuture<Void> closedFuture = new CompletableFuture<>();

    public NativeLibraryDialog(List<String> libraries, DbxPlugin source, boolean required) {
        super(DataCat.getInstance().getUi().getFrame(), translate("dialog.native-lib.load.title"), ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 420);
        setLocationRelativeTo(DataCat.getInstance().getUi().getFrame());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                closedFuture.complete(null);
                responseFuture.complete(-1);
            }
        });

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(15,15,15,15));

        JLabel header = new JLabel(translate("dialog.native-lib.load.message.optional", Map.of("plugin", source.getName())));
        header.setFont(header.getFont().deriveFont(Font.PLAIN, 14f));

        main.add(header, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        libraries.forEach(model::addElement);

        JList<String> list = new JList<>(model);
        list.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(480, 150));

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(scroll);
        center.add(Box.createVerticalStrut(15));

        JPanel warning = new JPanel();
        warning.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        warning.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,160,40)),
                new EmptyBorder(10,10,10,10)
        ));

        warning.setBackground(new Color(255, 245, 190));
        warning.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel warningTitle = new JLabel("dialog.native-lib.load.message.warning.title");
        warningTitle.setFont(warningTitle.getFont().deriveFont(Font.BOLD));

        JLabel warningText = new JLabel("dialog.native-lib.load.message.warning.message");

        warningTitle.setBackground(warning.getBackground());
        warningText.setBackground(warning.getBackground());

        warning.add(warningTitle);
        warning.add(Box.createVerticalStrut(5));
        warning.add(warningText);

        center.add(warning);

        main.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton trustButton = new JButton("Trust source and load");
        JButton loadButton = new JButton("Load");
        JButton cancelButton = new JButton("Cancel");

        trustButton.addActionListener(e -> {
            result = 0;
            dispose();
            responseFuture.complete(0);
        });

        loadButton.addActionListener(e -> {
            result = 1;
            dispose();
            responseFuture.complete(1);
        });

        cancelButton.addActionListener(e -> {
            result = 2;
            dispose();
            responseFuture.complete(2);
        });

        buttons.add(trustButton);
        buttons.add(loadButton);
        buttons.add(cancelButton);

        main.add(buttons, BorderLayout.SOUTH);

        setContentPane(main);
    }

    @Override
    public CompletableFuture<Integer> answer() {
        return responseFuture;
    }

    @Override
    public CompletableFuture<Void> closed() {
        return closedFuture;
    }
}