package com.pie.tlatoani.Miscellaneous.NoteBlock;

import ch.njol.skript.classes.Changer;
import com.pie.tlatoani.Util.ChangeablePropertyExpression;
import com.pie.tlatoani.Util.MundoUtil;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprNoteOfBlock extends ChangeablePropertyExpression<Block, Note> {

    @Override
    public void change(Block block, Note note, Changer.ChangeMode changeMode) {
        MundoUtil.cast(block, NoteBlock.class).ifPresent(noteBlock -> noteBlock.setNote(note));
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public Note convert(Block block) {
        return MundoUtil.cast(block, NoteBlock.class).map(NoteBlock::getNote).orElse(null);
    }
}
