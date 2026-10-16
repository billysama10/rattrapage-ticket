// Seul endroit du front qui parle à l'API : aucun composant n'appelle fetch directement.

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/tickets'

/** Erreur lisible par l'utilisateur, avec le code HTTP (0 = serveur injoignable). */
export class ErreurApi extends Error {
  constructor(message, status) {
    super(message)
    this.name = 'ErreurApi'
    this.status = status
  }
}

async function appeler(url, options) {
  let reponse
  try {
    reponse = await fetch(url, options)
  } catch {
    throw new ErreurApi(
      'Le serveur est injoignable. Vérifiez qu’il est démarré puis réessayez.',
      0,
    )
  }

  if (reponse.ok) {
    return reponse.json()
  }

  // le serveur renvoie { status, erreur, message, horodatage } : on affiche son message tel quel
  let message = `Le serveur a répondu avec une erreur (code ${reponse.status}).`
  try {
    const corps = await reponse.json()
    if (corps?.message) message = corps.message
  } catch {
    // corps absent ou non JSON : on garde le message par défaut
  }
  throw new ErreurApi(message, reponse.status)
}

export function listerTickets(statut) {
  const url = statut ? `${API_URL}?statut=${encodeURIComponent(statut)}` : API_URL
  return appeler(url)
}

export function creerTicket({ titre, description }) {
  return appeler(API_URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ titre, description }),
  })
}

export function fermerTicket(id) {
  return appeler(`${API_URL}/${id}/fermer`, { method: 'PATCH' })
}
