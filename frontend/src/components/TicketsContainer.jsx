import { useCallback, useEffect, useRef, useState } from 'react'
import { creerTicket, fermerTicket, listerTickets } from '../services/ticketService'
import TicketForm from './TicketForm'
import TicketList from './TicketList'
import ConfirmationFermeture from './ConfirmationFermeture'

// Conteneur : il porte l'état et fait les appels via le service ; les autres composants affichent.
function TicketsContainer() {
  const [tickets, setTickets] = useState([])
  const [statutFiltre, setStatutFiltre] = useState('')
  const [chargement, setChargement] = useState(true)
  const [erreurListe, setErreurListe] = useState(null)

  const [erreurFormulaire, setErreurFormulaire] = useState(null)
  const [creationEnCours, setCreationEnCours] = useState(false)

  const [ticketAFermer, setTicketAFermer] = useState(null)
  const [fermetureEnCours, setFermetureEnCours] = useState(false)
  const [erreurFermeture, setErreurFermeture] = useState(null)

  // ignore les réponses périmées si l'utilisateur change vite de filtre
  const dernierAppel = useRef(0)

  const charger = useCallback(async (statut, { silencieux = false } = {}) => {
    const numero = ++dernierAppel.current
    if (!silencieux) setChargement(true)
    setErreurListe(null)
    try {
      const resultat = await listerTickets(statut)
      if (numero !== dernierAppel.current) return
      setTickets(resultat)
    } catch (e) {
      if (numero !== dernierAppel.current) return
      setErreurListe(e.message)
    } finally {
      if (numero === dernierAppel.current) setChargement(false)
    }
  }, [])

  useEffect(() => {
    charger(statutFiltre)
  }, [charger, statutFiltre])

  async function creer(donnees) {
    setCreationEnCours(true)
    setErreurFormulaire(null)
    try {
      await creerTicket(donnees)
      await charger(statutFiltre, { silencieux: true })
      return true
    } catch (e) {
      setErreurFormulaire(e.message)
      return false
    } finally {
      setCreationEnCours(false)
    }
  }

  async function confirmerFermeture() {
    setFermetureEnCours(true)
    setErreurFermeture(null)
    try {
      await fermerTicket(ticketAFermer.id)
    } catch (e) {
      setErreurFermeture(e.message)
    } finally {
      setFermetureEnCours(false)
      setTicketAFermer(null)
      // dans tous les cas on resynchronise : en cas de 409 la liste montre l'état réel
      await charger(statutFiltre, { silencieux: true })
    }
  }

  return (
    <main className="page">
      <header>
        <h1>Gestion des tickets</h1>
        <p>Suivi des demandes du service support</p>
      </header>

      <TicketForm onCreer={creer} erreur={erreurFormulaire} enCours={creationEnCours} />

      {erreurFermeture && (
        <p className="message message-erreur" role="alert">
          {erreurFermeture}{' '}
          <button type="button" className="lien" onClick={() => setErreurFermeture(null)}>
            Masquer
          </button>
        </p>
      )}

      <TicketList
        tickets={tickets}
        chargement={chargement}
        erreur={erreurListe}
        statutFiltre={statutFiltre}
        onChangerFiltre={(statut) => {
          setErreurFermeture(null)
          setStatutFiltre(statut)
        }}
        onReessayer={() => charger(statutFiltre)}
        onDemanderFermeture={(ticket) => {
          setErreurFermeture(null)
          setTicketAFermer(ticket)
        }}
      />

      {ticketAFermer && (
        <ConfirmationFermeture
          ticket={ticketAFermer}
          enCours={fermetureEnCours}
          onConfirmer={confirmerFermeture}
          onAnnuler={() => setTicketAFermer(null)}
        />
      )}
    </main>
  )
}

export default TicketsContainer
