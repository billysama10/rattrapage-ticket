function ConfirmationFermeture({ ticket, enCours, onConfirmer, onAnnuler }) {
  return (
    <div className="voile" role="dialog" aria-modal="true" aria-labelledby="titre-confirmation">
      <div className="modale">
        <h2 id="titre-confirmation">Fermer ce ticket ?</h2>
        <p>
          Le ticket « <strong>{ticket.titre}</strong> » passera au statut FERME et ne pourra plus
          changer d&rsquo;état.
        </p>
        <div className="actions">
          <button type="button" className="secondaire" onClick={onAnnuler} disabled={enCours}>
            Annuler
          </button>
          <button type="button" onClick={onConfirmer} disabled={enCours}>
            {enCours ? 'Fermeture…' : 'Confirmer la fermeture'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default ConfirmationFermeture
