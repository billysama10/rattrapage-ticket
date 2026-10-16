import { useState } from 'react'

// Pas de maxLength sur le titre : le refus des titres trop longs doit venir du serveur.
function TicketForm({ onCreer, erreur, enCours }) {
  const [titre, setTitre] = useState('')
  const [description, setDescription] = useState('')

  async function soumettre(evenement) {
    evenement.preventDefault()
    const reussi = await onCreer({ titre, description: description.trim() || null })
    if (reussi) {
      setTitre('')
      setDescription('')
    }
  }

  return (
    <form className="formulaire" onSubmit={soumettre}>
      <h2>Nouveau ticket</h2>

      <label htmlFor="titre">Titre</label>
      <input
        id="titre"
        type="text"
        value={titre}
        onChange={(e) => setTitre(e.target.value)}
        placeholder="Ex. : Impossible de me connecter"
        aria-invalid={erreur ? 'true' : 'false'}
      />

      <label htmlFor="description">Description (facultative)</label>
      <textarea
        id="description"
        rows={3}
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />

      {erreur && (
        <p className="message message-erreur" role="alert">
          {erreur}
        </p>
      )}

      <button type="submit" disabled={titre.trim() === '' || enCours}>
        {enCours ? 'Création…' : 'Créer le ticket'}
      </button>
    </form>
  )
}

export default TicketForm
