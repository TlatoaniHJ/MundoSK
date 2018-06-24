package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffCreateSimpleTab extends Effect {
    private Expression<String> idExpression;
    private TablistProvider tablistProvider;
    private Expression<String> displayNameExpression;
    private Optional<Expression<Number>> latencyBarsExpression;
    private Optional<Expression<Skin>> iconExpression;
    private Optional<Expression<Number>> scoreExpression;

    @Override
    protected void execute(Event event) {
        String id = idExpression.getSingle(event);
        String displayName = this.displayNameExpression.getSingle(event);
        if (id == null || displayName == null) {
            return;
        }
        if (id.length() > 12) {
            Skript.warning("A script attempted to create a simple tab with the id \"" + id + "\", "
                        +  "but simple tab ids can't be longer than 12 characters so no tab was created.");
            return;
        }
        Integer latencyBars = latencyBarsExpression.map(expression -> expression.getSingle(event)).map(Number::intValue).orElse(null);
        Skin icon = iconExpression.map(expression -> expression.getSingle(event)).orElse(null);
        Integer score = scoreExpression.map(expression -> expression.getSingle(event)).map(Number::intValue).orElse(null);
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.createTab(id, displayName, latencyBars, icon, score);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("create simple tab " + idExpression
                + " [for %] with display name " + displayNameExpression
                + latencyBarsExpression.map(expr -> " latency bars " + expr).orElse("")
                + iconExpression.map(expr -> " icon " + expr).orElse("")
                + scoreExpression.map(expr -> " score " + expr).orElse(""));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        idExpression = (Expression<String>) expressions[0];
        tablistProvider = TablistProvider.of(expressions, 1);
        displayNameExpression = (Expression<String>) expressions[3];
        latencyBarsExpression = Optional.ofNullable((Expression<Number>) expressions[4]);
        iconExpression = Optional.ofNullable((Expression<Skin>) expressions[5]);
        scoreExpression = Optional.ofNullable((Expression<Number>) expressions[6]);
        return true;
    }
}
