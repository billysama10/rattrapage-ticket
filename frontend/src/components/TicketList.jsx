const STATUTS = [
  { valeur: '', libelle: 'Tous' },
  { valeur: 'OUVERT', libelle: 'OUVERT' },
  { valeur: 'EN_COURS', libelle: 'EN_COURS' },
  { valeur: 'FERME', libelle: 'FERME' },
]

const formatDate = (iso) =>
  new Date(iso).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' })

const peutFermer = (ticket) => ticket.statut === 'OUVERT' || ticket.statut === 'EN_COURS'

function TicketList({
  tickets,
  chargement,
  erreur,
  statutFiltre,
  onChangerFiltre,
  onReessayer,
  onDemanderFermeture,
}) {
  return (
    <section className="liste">
      <div className="liste-entete">
        <h2>Tickets</h2>
        <label className="filtre">
          Statut
          <select value={statutFiltre} onChange={(e) => onChangerFiltre(e.target.value)}>
            {STATUTS.map((s) => (
              <option key={s.valeur} value={s.valeur}>
                {s.libelle}
              </option>
            ))}
          </select>
        </label>
      </div>

      {chargement && (
        <p className="etat" role="status">
          <span className="spinner" aria-hidden="true" /> Chargement des tickets…
        </p>
      )}

      {!chargement && erreur && (
        <div className="etat etat-erreur" role="alert">
          <p>{erreur}</p>
          <button type="button" onClick={onReessayer}>
            Réessayer
          </button>
        </div>
      )}

      {!chargement && !erreur && tickets.length === 0 && (
        <p className="etat">Aucun ticket</p>
      )}

      {!chargement && !erreur && tickets.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Titre</th>
              <th>Statut</th>
              <th>Date de création</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {tickets.map((ticket) => (
              <tr key={ticket.id}>
                <td>
                  <strong>{ticket.titre}</strong>
                  {ticket.description && <div className="description">{ticket.description}</div>}
                </td>
                <td>
                  <span className={`badge badge-${ticket.statut}`}>{ticket.statut}</span>
                </td>
                <td>{formatDate(ticket.dateCreation)}</td>
                <td>
                  {peutFermer(ticket) && (
                    <button
                      type="button"
                      className="secondaire"
                      onClick={() => onDemanderFermeture(ticket)}
                    >
                      Fermer
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default TicketList
