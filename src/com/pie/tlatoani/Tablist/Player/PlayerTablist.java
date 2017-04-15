package com.pie.tlatoani.Tablist.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.OldTablist;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Tlatoani on 4/14/17.
 * Note: Currently, Personals given by PlayerPersonalizable allow you to change the icon
 * Please don't do it
 */
public class PlayerTablist {
    private final HashMap<Player, Optional<Tab>> tabs = new HashMap<>();
    public final OldTablist oldTablist;

    public static class PlayerTab extends Tab {

        public PlayerTab(OldTablist oldTablist, Player player) {
            super(oldTablist, player.getName(), player.getUniqueId(), null, null, null, null);
        }

        public PlayerTab(Tab prev) {
            super(prev);
        }

        @Override
        public void setIcon(Skin icon) {
            throw new IllegalArgumentException("PlayerTablist does not allow you to change skin icons, as that would change the player's own skin");
        }
    }

    public static class PlayerPersonalizable extends Tab.Personalizable {
        public final Player player;

        public PlayerPersonalizable(OldTablist oldTablist, Player player) {
            super(oldTablist, player.getName(), player.getUniqueId());
            this.player = player;
        }

        public PlayerPersonalizable(Player player, Tab prev) {
            super(prev);
            this.player = player;
        }

        @Override
        public void hideForAll() {
            super.hideForAll();
            oldTablist.playerTablist.tabs.put(player, Optional.empty());
        }

        @Override
        public void hideFor(Player player) {
            super.hideFor(player);
            if (!visibleByDefault && isUniform()) {
                oldTablist.playerTablist.tabs.put(player, Optional.empty());
            }
        }

        @Override
        public void removePlayer(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (personalOptional == null) {
                if (visibleByDefault) {
                    if (getDisplayName() != null) {
                    send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                }
                    if (getLatency() != null) {
                        send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                    }
                } else {
                    send(showPacket(), player);
                }
            } else {
                personalOptional.ifPresent(__ -> send(hidePacket(), player));
                personalTabs.remove(player);
                Mundo.optionalCase(personalOptional, personal -> {
                    if (personal.getDisplayName() != null) {
                        personal.send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null));
                    }
                    if (personal.getLatency() != null) {
                        personal.send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, null, null));
                    }
                }, () -> send(showPacket(), player));
            }
        }

        @Override
        public void removeIfApplicable(Personal personal) {
            super.removeIfApplicable(personal);
            if (isUniform()) {
                if (visibleByDefault) {
                    oldTablist.playerTablist.tabs.put(player, Optional.of(new PlayerTab(this)));
                } else {
                    oldTablist.playerTablist.tabs.put(player, Optional.empty());
                }
            }
        }

        @Override
        public void setIcon(Skin icon) {
            throw new IllegalArgumentException("PlayerTablist does not allow you to change skin icons, as that would change the player's own skin");
        }
    }

    public PlayerTablist(OldTablist oldTablist) {
        this.oldTablist = oldTablist;
    }

    public Tab getTab(Player player) {
        Optional<Tab> tabOptional = tabs.get(player);
        if (tabOptional == null) {
            return null;
        } else {
            return tabOptional.orElse(null);
        }
    }

    public Tab forceTab(Player player) {
        Optional<Tab> tabOptional = tabs.get(player);
        if (tabOptional == null) {
            Tab tab = new PlayerTab(oldTablist, player);
            tabs.put(player, Optional.of(tab));
            return tab;
        } else {
            return tabOptional.orElse(null);
        }
    }

    public PlayerPersonalizable forcePersonalizable(Player player) {
        return tabs.get(player).map(tab -> {
            if (tab instanceof PlayerPersonalizable) {
                return (PlayerPersonalizable) tab;
            }
            PlayerPersonalizable personalizable1 = new PlayerPersonalizable(player, tab);
            tabs.put(player, Optional.of(personalizable1));
            return personalizable1;
        }).orElseGet(() -> {
            PlayerPersonalizable personalizable1 = new PlayerPersonalizable(oldTablist, player);
            tabs.put(player, Optional.of(personalizable1));
            return personalizable1;
        });
    }

    public void addPlayer(Player player) {
        tabs.forEach(((objPlayer, tabOptional) -> {
            Mundo.optionalCase(tabOptional, tab -> {
                if (tab instanceof PlayerPersonalizable && !((PlayerPersonalizable) tab).isVisibleByDefault()) {
                    tab.send(tab.hidePacket(), player);
                } else {
                    if (tab.getDisplayName() != null) {
                        tab.send(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                    }
                    if (tab.getLatency() != null) {
                        tab.send(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, null, null), player);
                    }
                    if (tab.getScore() != null) {
                        tab.send(tab.updateScorePacket(tab.getScore()), player);
                    }
                }
            }, () -> {
                PacketContainer hidePacket = OldTablist.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                UtilPacketEvent.sendPacket(hidePacket, PlayerTablist.class, player);
            });
        }));
    }

    public void removePlayer(Player player) {
        tabs.forEach(((objPlayer, tabOptional) -> {
            Mundo.optionalCase(tabOptional, tab -> {
                if (tab instanceof PlayerPersonalizable) {
                    ((PlayerPersonalizable) tab).removePlayer(player);
                } else {
                    if (tab.getDisplayName() != null) {
                        tab.send(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                    }
                    if (tab.getLatency() != null) {
                        tab.send(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                    }
                }
            }, () -> {
                PacketContainer showPacket = OldTablist.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                UtilPacketEvent.sendPacket(showPacket, PlayerTablist.class, player);
            });
        }));
    }
}
