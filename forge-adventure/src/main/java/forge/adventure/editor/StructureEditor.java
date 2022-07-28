package forge.adventure.editor;

import forge.adventure.data.BiomeData;
import forge.adventure.data.BiomeStructureData;
import forge.adventure.data.BiomeTerrainData;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Editor class to edit configuration, maybe moved or removed
 */
public class StructureEditor extends JComponent{
    DefaultListModel<BiomeStructureData> model = new DefaultListModel<>();
    JList<BiomeStructureData> list = new JList<>(model);
    JToolBar toolBar = new JToolBar("toolbar");
    BiomeStructureEdit edit=new BiomeStructureEdit();

    BiomeData currentData;

    public class StructureDataRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(!(value instanceof BiomeTerrainData))
                return label;
            BiomeTerrainData structureData=(BiomeTerrainData) value;
            StringBuilder builder=new StringBuilder();
            builder.append("Structure");
            builder.append(" ");
            builder.append(structureData.spriteName);
            label.setText(builder.toString());
            return label;
        }
    }
    public void addButton(String name, ActionListener action)
    {
        JButton newButton=new JButton(name);
        newButton.addActionListener(action);
        toolBar.add(newButton);

    }

    public StructureEditor()
    {

        list.setCellRenderer(new StructureDataRenderer());
        list.addListSelectionListener(e -> StructureEditor.this.updateEdit());
        addButton("add", e -> StructureEditor.this.addStructure());
        addButton("remove", e -> StructureEditor.this.remove());
        addButton("copy", e -> StructureEditor.this.copy());
        BorderLayout layout=new BorderLayout();
        setLayout(layout);
        add(list, BorderLayout.LINE_START);
        add(toolBar, BorderLayout.PAGE_START);
        add(edit,BorderLayout.CENTER);


        edit.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                emitChanged();
            }
        });
    }
    protected void emitChanged() {
        ChangeListener[] listeners = listenerList.getListeners(ChangeListener.class);
        if (listeners != null && listeners.length > 0) {
            ChangeEvent evt = new ChangeEvent(this);
            for (ChangeListener listener : listeners) {
                listener.stateChanged(evt);
            }
        }
    }
    private void copy() {

        int selected=list.getSelectedIndex();
        if(selected<0)
            return;
        BiomeStructureData data=new BiomeStructureData(model.get(selected));
        model.add(model.size(),data);
    }

    private void updateEdit() {

        int selected=list.getSelectedIndex();
        if(selected<0)
            return;
        edit.setCurrentStructure(model.get(selected),currentData);
    }

    void addStructure()
    {
        BiomeStructureData data=new BiomeStructureData();
        model.add(model.size(),data);
    }
    void remove()
    {
        int selected=list.getSelectedIndex();
        if(selected<0)
            return;
        model.remove(selected);
    }
    public void setStructures(BiomeData data) {

        currentData=data;
        model.clear();
        if(data==null||data.structures==null)
            return;
        for (int i=0;i<data.structures.length;i++) {
            model.add(i,data.structures[i]);
        }
        list.setSelectedIndex(0);
    }

    public BiomeStructureData[] getBiomeStructureData() {

        BiomeStructureData[] rewards= new BiomeStructureData[model.getSize()];
        for(int i=0;i<model.getSize();i++)
        {
            rewards[i]=model.get(i);
        }
        return rewards;
    }
    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }
}
