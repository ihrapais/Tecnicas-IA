
% Base de Fatos
personagem(gandalf, humano, magico).
personagem(legolas, elfo, arqueiro).
personagem(thor, anao, guerreiro).
personagem(gimli, anao, guerreiro).
personagem(bilbo, duende, nenhum).
personagem(frodo, humano, nenhum).
personagem(aragorn, humano, guerreiro).
personagem(gollum, duende, nenhum).

% Regra: personagem é bom em combate à distância se for elfo ou arqueiro
bom_em_combate_distancia(X) :- personagem(X, elfo, _).
bom_em_combate_distancia(X) :- personagem(X, _, arqueiro).

% Regra: personagem é forte se for guerreiro ou da raça anão
forte(X) :- personagem(X, _, guerreiro).
forte(X) :- personagem(X, anao, _).

% Regra: personagem pode usar magia se for da classe mágico
pode_usar_magia(X) :- personagem(X, _, magico).

% Regra: personagem X se dá bem contra personagem Y conforme suas habilidades
se_dao_bem(X, Y) :- personagem(X, _, guerreiro), personagem(Y, _, magico).
se_dao_bem(X, Y) :- personagem(X, _, arqueiro), personagem(Y, _, guerreiro).
se_dao_bem(X, Y) :- personagem(X, _, magico), personagem(Y, _, guerreiro).
